document.getElementById('calculatePrice').addEventListener('click', function () {
    var form = document.getElementById('myForm');
    var formData = new FormData(form);
    fetch('/createPreis', {method: 'POST', body: formData}).then(response => response.json()).then(data => {
        document.getElementById('preis').textContent = data.preis;
    }).catch(error => {
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
        formData.forEach((value, key) => {
            jsonData[key] = value;
        });

        fetch('/calculatePreis', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(jsonData)
        })
            .then(response => response.json())
            .then(data => {
                preisDiv.textContent = data.preis;
            })
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

startInput.min = todayDate;
birthInput.min = maxbirth;
birthInput.max = minbirth;
endInput.min = todayDate;
endInput.min = todayDate;
createInput.max = todayDate;