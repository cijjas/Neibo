<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Title</title>
</head>
<body>
<h1>The following information will be brought through the API</h1>

<h3 id="postContent"></h3>
<h4 id="commentPlaceholder"></h4>
<img id="imagePlaceholder" src="" alt="Image">

<script>
  // Function to fetch a comment from the API and update the page
  async function getComment() {
    try {
      const response = await fetch("/endpoint/comment?id=2");
      if (!response.ok) {
        throw new Error("Failed to fetch data from the API.");
      }
      // Update the content of the <h4> element with the fetched comment
      document.getElementById("commentPlaceholder").textContent = await response.text();
    } catch (error) {
      console.error(error.message);
    }
  }
  getComment();



  // Function to fetch an image from the API and display it
  async function getImage() {
    try {
      const response = await fetch("/endpoint/image?id=1"); // Replace with the appropriate image ID
      if (!response.ok) {
        throw new Error("Failed to fetch the image from the API.");
      }
      const blob = await response.blob();
      const imageUrl = URL.createObjectURL(blob);
      // Update the src attribute of the <img> element with the fetched image URL
      document.getElementById("imagePlaceholder").src = imageUrl;
    } catch (error) {
      console.error(error.message);
    }
  }
  getImage();

  // Function to fetch a post from the API and display it
  async function getPost() {
    try {
      const response = await fetch("/endpoint/posts?id=2"); // Replace with the appropriate post ID
      if (!response.ok) {
        throw new Error("Failed to fetch post data from the API.");
      }
      // Update the content of the <p> element with the fetched post content
      document.getElementById("postContent").textContent = await response.text();
    } catch (error) {
      console.error(error.message);
    }
  }
  getPost();
</script>
</body>
</html>
