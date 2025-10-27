let currentBoardName = decodeURIComponent(new URLSearchParams(window.location.search).get('board') || 'Default');
let currentTaskForEdit = null;
let currentTaskForDelete = null;

document.getElementById('boardTitle').textContent = `${currentBoardName} Kanban Board`;

// Load tasks for all stages
async function loadBoard() {
    const stages = ['TO-DO', 'IN-PROGRESS', 'DONE'];
    for (const stage of stages) {
        await loadTasks(stage);
    }
}

async function loadTasks(stage) {
    fetchData(`/boards/${currentBoardName}/stages/${stage}/tasks`).then(tasks => {
        const ul = document.getElementById(`${stage.toLowerCase()}-tasks`);
        ul.innerHTML = tasks.map(task => `
            <li class="task ${task.priority.toLowerCase()}" draggable="true" data-id="${task.id}" ondragstart="drag(event)" onclick="openEditModal('${task.id}')">
                <strong>${task.title}</strong><br>
                ${task.description || ''}<br>
                Assignee: ${task.assignee || 'Unassigned'} | Pri: ${task.priority}
                <button onclick="openDeleteTaskModal('${task.id}', event)" class="btn-danger" style="float: right; padding: 0.25rem;">Delete</button>
            </li>
        `).join('');
        document.getElementById(`${stage.toLowerCase()}-count`).textContent = `(${tasks.length})`;
    });
}

function openAddModal(stage) {
    currentStage = stage;
    document.getElementById('taskTitle').value = '';
    document.getElementById('taskDesc').value = '';
    document.getElementById('taskAssignee').value = '';
    document.getElementById('taskPriority').value = 'Medium';
    document.getElementById('taskDeps').value = '';
    openModal('addModal');
}

let currentStage;
async function addTask() {
    const taskData = {
        title: document.getElementById('taskTitle').value.trim(),
        description: document.getElementById('taskDesc').value,
        assignee: document.getElementById('taskAssignee').value,
        priority: document.getElementById('taskPriority').value,
        dependencies: document.getElementById('taskDeps').value.split(',').map(d => d.trim()).filter(d => d)
    };
    if (!taskData.title) return alert('Title required');
    try {
        const response = await fetch(`${API_BASE}/boards/${currentBoardName}/tasks?stage=${currentStage}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(taskData)
        });
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || `HTTP ${response.status}`);
        }
        const result = await response.json();
        console.log('Task added:', result); // Debug log
        closeModal('addModal');
        loadTasks(currentStage);
    } catch (error) {
        console.error('Add task error:', error);
        alert('Failed to add task: ' + error.message);
    }
}

function openEditModal(taskId) {
    fetchData(`/boards/${currentBoardName}/tasks/${taskId}`).then(task => {
        currentTaskForEdit = task;
        document.getElementById('editTaskTitle').value = task.title;
        document.getElementById('editTaskDesc').value = task.description;
        document.getElementById('editTaskAssignee').value = task.assignee;
        document.getElementById('editTaskPriority').value = task.priority;
        document.getElementById('editTaskDeps').value = task.dependencies.join(', ');
        openModal('editModal');
    });
}

function updateTask() {
    const updatedData = {
        title: document.getElementById('editTaskTitle').value,
        description: document.getElementById('editTaskDesc').value,
        assignee: document.getElementById('editTaskAssignee').value,
        priority: document.getElementById('editTaskPriority').value,
        dependencies: document.getElementById('editTaskDeps').value.split(',').map(d => d.trim()).filter(d => d)
    };
    fetchData(`/boards/${currentBoardName}/tasks/${currentTaskForEdit.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updatedData)
    }).then(() => {
        closeModal('editModal');
        loadBoard(); // Refresh all
    });
}

function openDeleteTaskModal(taskId, e) {
    e.stopPropagation();
    currentTaskForDelete = taskId;
    openModal('deleteTaskModal');
}

document.getElementById('confirmDeleteTask').onclick = () => {
    fetchData(`/boards/${currentBoardName}/tasks/${currentTaskForDelete}`, { method: 'DELETE' }).then(() => {
        closeModal('deleteTaskModal');
        loadBoard();
    });
};

function openRenameModal() {
    document.getElementById('renameBoardName').value = currentBoardName;
    openModal('renameBoardModal');
}

function renameCurrentBoard() {
    const newName = document.getElementById('renameBoardName').value.trim();
    fetchData(`/boards/${currentBoardName}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: newName })
    }).then(() => {
        closeModal('renameBoardModal');
        currentBoardName = newName;
        document.getElementById('boardTitle').textContent = `${newName} Kanban Board`;
    });
}

document.getElementById('confirmDeleteBoard').onclick = () => {
    fetchData(`/boards/${currentBoardName}`, { method: 'DELETE' }).then(() => {
        closeModal('deleteBoardModal');
        window.location.href = 'index.html';
    });
};

function backToManager() {
    window.location.href = 'index.html';
}

function refreshBoard() {
    loadBoard();
}

// Drag-Drop Functions
function allowDrop(e) {
    e.preventDefault();
}

function drag(e) {
    e.dataTransfer.setData('text', e.target.dataset.id);
}

function drop(e) {
    e.preventDefault();
    const taskId = e.dataTransfer.getData('text');
    const stage = e.currentTarget.dataset.stage;
    fetch(`${API_BASE}/boards/${currentBoardName}/tasks/${taskId}/move?toStage=${stage}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ toStage: stage })
    }).then(response => {
        if (!response.ok) {
            return response.json().then(errorData => {
                throw new Error(errorData.error || `HTTP ${response.status}`);
            });
        }
        return response.json();
    }).then(result => {
        console.log('Moved:', result); // Success log
        loadBoard();
    }).catch(error => {
        console.error('Move error:', error); // Full error
        alert('Move failed: ' + error.message); // e.g., "Move failed (deps or error)"
    });
}

// Search/Filter
function filterTasks() {
    const query = document.getElementById('searchInput').value.toLowerCase();
    const priority = document.getElementById('filterPriority').value;
    const tasks = document.querySelectorAll('.task');
    tasks.forEach(task => {
        const text = task.textContent.toLowerCase();
        const pri = task.classList[1]; // e.g., 'high'
        const matches = text.includes(query) && (priority === '' || pri === priority.toLowerCase());
        task.style.display = matches ? 'block' : 'none';
    });
}

// Initial Load
loadBoard();