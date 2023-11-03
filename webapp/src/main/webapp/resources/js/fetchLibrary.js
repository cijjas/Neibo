async function getImageInto(imageElementId, imageSrcId, contextPath) {
    let image = document.getElementById(imageElementId);
    if (imageSrcId === 0) {
        image.src = contextPath + "/resources/images/roundedPlaceholder.png";
        image.classList.remove('placeholder');
        return;
    }
    else if(imageSrcId === -1){
        image.src = contextPath + "/resources/images/workersBackground.png";
        image.classList.remove('placeholder');
        return;
    }
    else if(imageSrcId === -2){
        image.src = contextPath + "/resources/images/no-images.jpg";
        image.classList.remove('placeholder');
        return;
    }
    try {
        const response = await fetch(contextPath + '/images/' + imageSrcId);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        const blob = await response.blob();

        setTimeout(() => {
            image.classList.remove('placeholder');
            image.src = URL.createObjectURL(blob);
        }, 500);

    } catch (e) {
        image.src = contextPath + "/resources/images/errorImage.png";
    }
}