


const tagInput1 = new TagsInput({
    selector: 'tag-input1',
    wrapperClass: 'tags-input-wrapper',
    duplicate: false,
    max: 5
});


let tagsString;

function submitForm() {
    document.getElementById("loader-container").style.display = "flex";
    clearImage();
    const tagsArray = tagInput1.arr;
    tagsString = tagsArray.join(',');
    document.getElementById('tags-input').value = tagsString;

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