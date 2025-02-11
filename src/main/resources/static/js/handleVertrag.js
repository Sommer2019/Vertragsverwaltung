function toggleEdit() {
    const handledVertrag = document.getElementById("handledVertrag");
    const inputs = handledVertrag.querySelectorAll("input, #bundesland, label, #preiscalc, #kmh");
    const flex = handledVertrag.querySelectorAll(".radio-group, .flex-container");
    flex.forEach(input => input.style.display = "flex");
    inputs.forEach(input => input.style.display = "block");
    document.getElementById("editContainer").style.display = "block";
    document.getElementById("menuContainer").style.display = "none";
    document.getElementById('editVisible').value = 'true';
}



function hideEdit() {
    const handledVertrag = document.getElementById("handledVertrag");
    const inputs = handledVertrag.querySelectorAll("#editContainer, input, #bundesland, label, #preiscalc, #kmh");
    inputs.forEach(input => input.style.display = "none");
    document.getElementById("editContainer").style.display = "none";
    document.getElementById("menuContainer").style.display = "block";
    document.getElementById('editVisible').value = 'false';
}

function toggleDelete() {
    const deleteContainer = document.getElementById('deleteContainer');
    const menuContainer = document.getElementById("menuContainer");
    deleteContainer.style.display = 'block';
    menuContainer.style.display = menuContainer.style.display === "none" ? "block" : "none";
}

function hideDelete() {
    const deleteContainer = document.getElementById('deleteContainer');
    const menuContainer = document.getElementById("menuContainer");
    deleteContainer.style.display = 'none';
    menuContainer.style.display = menuContainer.style.display === "block" ? "none" : "block";
}

document.getElementById('calculatePrice').addEventListener('click', function () {
    const form = document.getElementById('myForm');
    const formData = new FormData(form);
    fetch('/createPreis', {method: 'POST', body: formData})
        .then(response => response.json())
        .then(data => document.getElementById('preis').textContent = data.preis)
        .catch(error => {
            console.error('Fehler:', error);
            alert('Es gab einen Fehler bei der Berechnung des Preises.');
        });
});
document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('myForm');
    const calculateButton = document.getElementById('calculateButton');
    const preisDiv = document.getElementById('preis');

    calculateButton.addEventListener('click', function () {
        const formData = new FormData(form);
        const jsonData = {};
        formData.forEach((value, key) => jsonData[key] = value);

        fetch('/calculatePreis', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(jsonData)
        })
            .then(response => response.json())
            .then(data => preisDiv.textContent = data.preis)
            .catch(error => console.error('Error:', error));
    });
});

const startInput = document.getElementById('start');
const endInput = document.getElementById('end');
const createInput = document.getElementById('create');
const todayDate = getTodayDate();
const minbirth = getMinBirth();
const maxbirth = getMaxBirth();
const birthInput = document.getElementById('birth');

function getTodayDate() {
    const today = new Date();
    return `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
}

function getMaxBirth() {
    const today = new Date();
    return `${today.getFullYear() - 100}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
}

function getMinBirth() {
    const today = new Date();
    return `${today.getFullYear() - 18}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
}

startInput.addEventListener('change', () => {
    const startDate = startInput.value;
    endInput.min = startDate;
    createInput.max = startDate;
});

startInput.min = document.getElementById('startSpan').textContent.trim();
birthInput.min = maxbirth;
birthInput.max = minbirth;
endInput.min = document.getElementById('endSpan').textContent.trim();
createInput.max = todayDate;

