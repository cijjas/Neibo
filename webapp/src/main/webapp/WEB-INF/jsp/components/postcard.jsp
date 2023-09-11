<%--
  Created by IntelliJ IDEA.
  User: chrij
  Date: 9/11/2023
  Time: 11:46 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="card-container">
    <!-- Post information -->
    <div class="post-section">
        <div class="post-info">
            <h2>${post.title}</h2>
            <p>Publicado por: ${post.neighbor.name}</p>
            <p>Mensaje: ${post.description}</p>
        </div >

    <!-- Image section -->
    <c:if test="${not empty post.imageFile}">
        <div style="display: flex; justify-content: center; align-items: center;">
            <img id="imageFile" src="data:image/jpg;base64,<c:out value='${post.imageFile}'/>" style="max-width: 100%; max-height: 100vh;">
        </div>
    </c:if>

    </div>



    <!-- Tag section -->
    <div class="tag-section">
        <div class="tag-list">
            <c:choose>
                <c:when test="${empty tags}">
                </c:when>
                <c:otherwise>
                    <p><strong> Tags :</strong> </p>
                    <c:forEach var="tag" items="${tags}">
                        <span class="badge badge-primary">#${tag.tag}</span>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>


    <!-- Comment section -->
    <div class="comments-section">
        <!-- Input box for adding a new comment -->
        <div class="comments-section-divider">
            <h5 class="divider-title">Sección de comentarios</h5>
            <hr class="divider-line">
        </div>

        <form>
            <!-- Name input -->
            <div class="form-row">
                <!-- First row -->
                <div class="form-outline col-md-6 mb-4">
                    <input type="text" id="nombre-comentario" class="form-control" />
                    <label class="form-label" for="nombre-comentario">Nombre</label>
                </div>
                <div class="form-outline col-md-6 mb-4">
                    <input type="text" id="apellido-comentario" class="form-control" />
                    <label class="form-label" for="apellido-comentario">Apellido</label>
                </div>
            </div>
            <div class="form-row">
                <!-- Second row -->
                <div class="form-outline col-md-6 mb-4">
                    <input type="text" id="mail-comentario" class="form-control" />
                    <label class="form-label" for="mail-comentario">Mail</label>
                </div>
                <div class="form-outline col-md-6 mb-4">
                    <input type="text" id="barrio-comentario" class="form-control" />
                    <label class="form-label" for="barrio-comentario">Barrio</label>
                </div>
            </div>

            <!-- Message input -->
            <div class="form-outline mb-4">
                <textarea class="form-control" id="mensaje-comentario" rows="4"></textarea>
                <label class="form-label" for="mensaje-comentario">Comentario</label>
            </div>

            <!-- Submit button -->
            <button type="submit" class="filter-button">
                Comentar
            </button>
        </form>

        <c:choose>
            <c:when test="${empty comments}">
                <div class="comment">
                    <div class="comment-body">
                        Aún no hay comentarios
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="comment" items="${comments}">
                    <div class="comment">
                        <div class="comment-header">
                            <strong>User #${comment.neighborId}</strong>
                        </div>
                        <div class="comment-body">
                                ${comment.comment}
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>


</div>