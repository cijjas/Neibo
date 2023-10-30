(function () {

    "use strict"

    // Plugin Constructor
    const ProfessionsInput = function (opts) {
        this.options = Object.assign(ProfessionsInput.defaults, opts);
        this.init();
    };

    // Initialize the plugin
    ProfessionsInput.prototype.init = function (opts) {
        this.options = opts ? Object.assign(this.options, opts) : this.options;

        if (this.initialized)
            this.destroy();

        if (!(this.orignal_input = document.getElementById(this.options.selector))) {
            console.error("professions-input couldn't find an element with the specified ID");
            return this;
        }

        this.arr = [];
        this.wrapper = document.createElement('div');
        this.input = document.createElement('input');
        init(this);
        initEvents(this);

        this.initialized = true;
        return this;
    }

    // Add Professions
    // Add Professions
    ProfessionsInput.prototype.addProfession = function (string) {
        if (this.anyErrors(string))
            return;
        if (!this.professionExistsInOptions(string)) {
            return;
        }


        this.arr.push(string);
        var professionInput = this;

        var profession = document.createElement('span');
        profession.className = this.options.professionClass;
        profession.innerText = string;

        var closeIcon = document.createElement('a');
        closeIcon.innerHTML = '&times;';

        // delete the profession when the icon is clicked
        closeIcon.addEventListener('click', function (e) {
            e.preventDefault();
            var profession = this.parentNode;

            for (var i = 0; i < professionInput.wrapper.childNodes.length; i++) {
                if (professionInput.wrapper.childNodes[i] === profession)
                    professionInput.deleteProfession(profession, i);
            }
        });

        profession.appendChild(closeIcon);
        this.wrapper.insertBefore(profession, this.input);
        this.orignal_input.value = this.arr.join(',');

        return this;
    }

    // Check if the profession exists in the list of available professions
    ProfessionsInput.prototype.professionExistsInOptions = function (profession) {
        const professionOptions = document.querySelectorAll('.profession-option');
        for (var i = 0; i < professionOptions.length; i++) {
            if (professionOptions[i].textContent === profession) {
                return true;
            }
        }
        return false;
    };

    // Delete Professions
    ProfessionsInput.prototype.deleteProfession = function (profession, i) {
        profession.remove();
        this.arr.splice(i, 1);
        this.orignal_input.value = this.arr.join(',');
        return this;
    }

    // Function to clear the commaeparated professions array
    ProfessionsInput.prototype.clearAllProfessions = function () {
        this.arr = [];
        this.orignal_input.value = '';
        this.wrapper.querySelectorAll('.profession').forEach(function (profession) {
            profession.remove();
            }
        );


    }


    // Make sure input string have no error with the plugin
    ProfessionsInput.prototype.anyErrors = function (string) {
        if (this.options.max != null && this.arr.length >= this.options.max) {
            return true;
        }

        if (!this.options.duplicate && this.arr.indexOf(string) !== -1) {
            return true;
        }

        return false;
    }

    // Add professions programmatically
    ProfessionsInput.prototype.addData = function (array) {
        const plugin = this;

        array.forEach(function (string) {
            plugin.addProfession(string);
        })
        return this;
    }

    // Get the Input String
    ProfessionsInput.prototype.getInputString = function () {
        return this.arr.join(',');
    }

    // destroy the plugin
    ProfessionsInput.prototype.destroy = function () {
        this.orignal_input.removeAttribute('hidden');

        delete this.orignal_input;
        var self = this;

        Object.keys(this).forEach(function (key) {
            if (self[key] instanceof HTMLElement)
                self[key].remove();

            if (key !== 'options')
                delete self[key];
        });

        this.initialized = false;
    }

    // Private function to initialize the profession input plugin
    function init(professions) {
        professions.wrapper.append(professions.input);
        professions.wrapper.classList.add(professions.options.wrapperClass);
        professions.orignal_input.setAttribute('hidden', 'true');
        professions.orignal_input.parentNode.insertBefore(professions.wrapper, professions.orignal_input);
    }


    // initialize the Events
    function initEvents(professions) {


        professions.input.placeholder = document.getElementById('niakaniaka').value;


        professions.wrapper.addEventListener('click', function () {
            professions.input.focus();
        });

        professions.input.addEventListener('keydown', function (e) {
            const str = professions.input.value.trim();

            if (![13, 188, 32].includes(e.keyCode)) {
                // If the key pressed is not Enter, Comma, or Space
                filterProfessions(professions, str);
            } else {
                e.preventDefault();
                professions.input.value = '';
                if (str !== '') professions.addProfession(str);

                clearFilteredProfessions();
            }

        });

    }

    function insertClearButton(professions) {
        const clearButton = document.createElement('a');
        clearButton.className = 'w-100 cool-button red';
        clearButton.innerHTML = '<spring:message code="Clear.all.professions"/>';
        clearButton.style.fontWeight = 'bolder';
        clearButton.onclick = function () {
            professions.clearAllProfessions();
            // Remove the clear button after clearing the professions
            clearButton.remove();
        };

        professions.wrapper.insertBefore(clearButton, professions.input);
    }

    // Set All the Default Values
    ProfessionsInput.defaults = {
        selector: '',
        wrapperClass: 'professions-input-wrapper',
        professionClass: 'profession',
        max: null,
        duplicate: false
    }

    window.ProfessionsInput = ProfessionsInput;

})();