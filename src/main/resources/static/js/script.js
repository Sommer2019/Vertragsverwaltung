const kennzeichenInput = document.getElementById('kennzeichen');
const herstellerInput = document.getElementById('hersteller');
const updatePreis = document.getElementById('preisedit');
const vornameInput = document.getElementById('vorname');
const nachnameInput = document.getElementById('nachname');
const typInput = document.getElementById('typ');
const strasseInput = document.getElementById('strasse');
const hausnummerInput = document.getElementById('hausnummer');
const plzInput = document.getElementById('plz');
const stadtInput = document.getElementById('stadt');
const bundeslandInput = document.getElementById('bundesland');
const regexKennzeichen = /^\p{Lu}{1,3}-\p{Lu}{1,2}\d{1,4}[EH]?$/u;
const regexName = /^[a-zA-Z0-9\s-äöüÄÖÜçéèêáàâíìîóòôúùûñÑ'-]+$/;
const regexTyp = /^[a-zA-Z0-9\s-äöüÄÖÜçéèêáàâíìîóòôúùûñÑ]+$/;
const regexPLZ = /^\d{5}$/;
const regexHausnummer = /^\d+[a-zA-Z]?$/;

function validateInputHersteller(input) {
    fetch('brands.json')
        .then(response => response.json())
        .then(data => {
            const brands = data.brands;
            const inputValue = `'${input.value.trim()}'`;
            input.classList.toggle('invalid', !brands.includes(inputValue));
            input.classList.toggle('valid', brands.includes(inputValue));
        })
        .catch(error => console.error('Error loading brands:', error));
}

function updatePrice() {
    fetch('/calculatePrice', { method: 'GET' })
        .then(response => response.json())
        .then(data => document.getElementById('preis').innerText = `${data.price} €`)
        .catch(error => console.error('Error:', error));
}

function validateInput(input, regex) {
    input.classList.toggle('invalid', !regex.test(input.value));
    input.classList.toggle('valid', regex.test(input.value));
}

kennzeichenInput.addEventListener('input', () => checkKennzeichen(kennzeichenInput, regexKennzeichen));
herstellerInput.addEventListener('input', () => validateInputHersteller(herstellerInput));
updatePreis.addEventListener('click', updatePrice);
vornameInput.addEventListener('input', () => validateInput(vornameInput, regexName));
nachnameInput.addEventListener('input', () => validateInput(nachnameInput, regexName));
typInput.addEventListener('input', () => validateInput(typInput, regexTyp));
strasseInput.addEventListener('input', () => validateInput(strasseInput, regexName));
hausnummerInput.addEventListener('input', () => validateInput(hausnummerInput, regexHausnummer));
plzInput.addEventListener('input', () => validateInput(plzInput, regexPLZ));
stadtInput.addEventListener('input', () => validateInput(stadtInput, regexName));
bundeslandInput.addEventListener('input', () => validateInput(bundeslandInput, regexName));


function checkKennzeichen(input, regex) {
    if (regex.test(input.value)) {
        fetch('vertrage.json')
            .then(response => response.json())
            .then(data => {
                const kennzeichenExists = data.some(vertrag => vertrag.fahrzeug.amtlichesKennzeichen === input.value.trim());
                input.classList.toggle('invalid', kennzeichenExists);
                input.classList.toggle('valid', !kennzeichenExists);
            })
            .catch(error => console.error('Error loading vertrage:', error));
    } else {
        input.classList.add('invalid');
        input.classList.remove('valid');
    }
}

