let currentBoardForDelete = '';
let currentBoardForRename = '';

async function loadBoards() {
    try {
        const response = await fetch(`${API_BASE}/boards`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const boards = await response.json();
        const list = document.getElementById('boardList');
        if (!boards || boards.length === 0) {
            list.innerHTML = '<p>No boards. Create one!</p>';
            return;
        }
        list.innerHTML = boards.map(board => {
            const name = board.name || board; // Handle if backend returns string array or object
            return `
                <div class="board-item">
                    <h3>${name}</h3>
                    <p>${board.description || 'Default board'}</p>
                    <button onclick="openKanban('${encodeURIComponent(name)}')" class="btn-primary">Open</button>
                    <button onclick="openRenameModal('${name}')" class="btn-secondary">Rename</button>
                    <button onclick="openDeleteModal('${name}')" class="btn-danger">Delete</button>
                </div>
            `;
        }).join('');
    } catch (error) {
        console.error('Error loading boards:', error);
        document.getElementById('boardList').innerHTML = '<p>Error loading boards. Check backend.</p>';
    }
}

function openCreateModal() {
    openModal('createModal');
    document.getElementById('boardName').value = '';
}

function createBoard() {
    const name = document.getElementById('boardName').value.trim();
    if (!name) return alert('Name required');
    fetchData('/boards', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name })
    }).then(() => {
        closeModal('createModal');
        loadBoards();
    });
}

function openKanban(boardName) {
    window.location.href = `kanban.html?board=${encodeURIComponent(boardName)}`;
}

function openDeleteModal(boardName) {
    currentBoardForDelete = boardName;
    openModal('deleteModal');
}

function confirmDelete() {
    fetchData(`/boards/${currentBoardForDelete}`, { method: 'DELETE' }).then(() => {
        closeModal('deleteModal');
        loadBoards();
    });
}

document.getElementById('confirmDelete').onclick = confirmDelete;

function openRenameModal(boardName) {
    currentBoardForRename = boardName;
    document.getElementById('newBoardName').value = boardName;
    openModal('renameModal');
}

function renameBoard() {
    const newName = document.getElementById('newBoardName').value.trim();
    if (!newName) return alert('New name required');
    fetchData(`/boards/${currentBoardForRename}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: newName })
    }).then(() => {
        closeModal('renameModal');
        loadBoards();
    });
}

function refreshBoards() {
    loadBoards();
}

// Initial load
loadBoards();