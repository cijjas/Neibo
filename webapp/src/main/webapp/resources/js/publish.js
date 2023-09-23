(function(){

    "use strict"

    // Plugin Constructor
    const TagsInput = function (opts) {
        this.options = Object.assign(TagsInput.defaults, opts);
        this.init();
    };

    // Initialize the plugin
    TagsInput.prototype.init = function(opts){
        this.options = opts ? Object.assign(this.options, opts) : this.options;

        if(this.initialized)
            this.destroy();

        if(!(this.orignal_input = document.getElementById(this.options.selector)) ){
            console.error("tags-input couldn't find an element with the specified ID");
            return this;
        }

        this.arr = [];
        this.wrapper = document.createElement('div');
        this.input = document.createElement('input');
        init(this);
        initEvents(this);

        this.initialized =  true;
        return this;
    }

    // Add Tags
    // Add Tags
    TagsInput.prototype.addTag = function(string) {
        if (this.anyErrors(string))
            return;

        this.arr.push(string);
        var tagInput = this;

        var tag = document.createElement('span');
        tag.className = this.options.tagClass;
        tag.innerText = string;

        var closeIcon = document.createElement('a');
        closeIcon.innerHTML = '&times;';

        // delete the tag when the icon is clicked
        closeIcon.addEventListener('click', function(e) {
            e.preventDefault();
            var tag = this.parentNode;

            for (var i = 0; i < tagInput.wrapper.childNodes.length; i++) {
                if (tagInput.wrapper.childNodes[i] === tag)
                    tagInput.deleteTag(tag, i);
            }
        });

        tag.appendChild(closeIcon);
        this.wrapper.insertBefore(tag, this.input);
        this.orignal_input.value = this.arr.join(',');

        return this;
    }

    // Delete Tags
    TagsInput.prototype.deleteTag = function(tag , i){
        tag.remove();
        this.arr.splice( i , 1);
        this.orignal_input.value =  this.arr.join(',');
        return this;
    }

    // Make sure input string have no error with the plugin
    TagsInput.prototype.anyErrors = function(string){
        if( this.options.max != null && this.arr.length >= this.options.max ){
            console.log('max tags limit reached');
            return true;
        }

        if(!this.options.duplicate && this.arr.indexOf(string) !== -1 ){
            console.log('duplicate found " '+string+' " ')
            return true;
        }

        return false;
    }

    // Add tags programmatically
    TagsInput.prototype.addData = function(array){
        const plugin = this;

        array.forEach(function(string){
            plugin.addTag(string);
        })
        return this;
    }

    // Get the Input String
    TagsInput.prototype.getInputString = function(){
        return this.arr.join(',');
    }

    // destroy the plugin
    TagsInput.prototype.destroy = function(){
        this.orignal_input.removeAttribute('hidden');

        delete this.orignal_input;
        var self = this;

        Object.keys(this).forEach(function(key){
            if(self[key] instanceof HTMLElement)
                self[key].remove();

            if(key !== 'options')
                delete self[key];
        });

        this.initialized = false;
    }

    // Private function to initialize the tag input plugin
    function init(tags){
        tags.wrapper.append(tags.input);
        tags.wrapper.classList.add(tags.options.wrapperClass);
        tags.orignal_input.setAttribute('hidden' , 'true');
        tags.orignal_input.parentNode.insertBefore(tags.wrapper , tags.orignal_input);
    }

    // initialize the Events
    function initEvents(tags){


        tags.input.placeholder = document.getElementById('niakaniaka').value;

        tags.wrapper.addEventListener('click' ,function(){
            tags.input.focus();
        });

        tags.input.addEventListener('keydown' , function(e){
            const str = tags.input.value.trim();

            if( !!(~[ 13 , 188, 32].indexOf( e.keyCode ))  )
            {
                e.preventDefault();
                tags.input.value = "";
                if(str !== "")
                    tags.addTag(str);
            }

        });
        tags.input.addEventListener('blur', function(e){
            const str = tags.input.value.trim();
            if (str !== "") {
                tags.addTag(str);
                tags.input.value = ""; // Clear the input field
            }


        });
    }


    // Set All the Default Values
    TagsInput.defaults = {
        selector : '',
        wrapperClass : 'tags-input-wrapper',
        tagClass : 'tag',
        max : null,
        duplicate: false
    }

    window.TagsInput = TagsInput;


})();


const tagInput1 = new TagsInput({
    selector: 'tag-input1',
    wrapperClass: 'tags-input-wrapper',
    duplicate: false,
    max: 5
});


let tagsString;

function submitForm() {
    clearImage();
    const tagsArray = tagInput1.arr; // Assuming tagInput1 has an 'arr' property with the tags

    tagsString = tagsArray.join(',');
    document.getElementById('tags-input').value = tagsString;
    //document.getElementById('submit-publishForm-button').submit();
}



// UPLOAD IMAGE JS

const dropContainer = document.getElementById("dropcontainer")
const fileInput = document.getElementById("images")

dropContainer.addEventListener("dragover", (e) => {
    // prevent default to allow drop
    e.preventDefault()
}, false)

dropContainer.addEventListener("dragenter", () => {
    dropContainer.classList.add("drag-active")
})

dropContainer.addEventListener("dragleave", () => {
    dropContainer.classList.remove("drag-active")
})

dropContainer.addEventListener("drop", (e) => {
    e.preventDefault()
    dropContainer.classList.remove("drag-active")
    fileInput.files = e.dataTransfer.files
})

// Show images preview
function preview() {

    let frame = document.getElementById('frame');
    if(!frame.src || frame.src === "") {
        frame.style.display = "none";
    } else {
        frame.src = URL.createObjectURL(event.target.files[0]);
        frame.style.display = "block";
    }
}
function clearImage() {
    document.getElementById('frame').value = null;
    let frame = document.getElementById('frame');
    frame.src = "";
}