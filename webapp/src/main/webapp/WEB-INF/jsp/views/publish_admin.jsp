<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<%@ include file="/WEB-INF/jsp/components/head.jsp" %>

<body>
<!-- Navigation Bar -->
<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>

<!-- Post Creation Form -->
<div class="container mt-4">
    <div class="card">
        <div class="card-body">
            <h4 class="card-title"><spring:message code="CreateAdminPost.title"/></h4>
            <%--            <form:errors cssClass="error" element="p"/>--%>
            <%--            <form:label path="email">Email:--%>
            <%--                <form:input path="email"/>--%>
            <%--            </form:label>--%>
            <%--            <form:errors path="email" cssClass="error" element="p"/>--%>
            <form:form method="post" action="/publish" modelAttribute="publishForm" enctype="multipart/form-data">
                <form:errors cssClass="error" element="p"/>
                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2">
                        <form:label path="name"> <spring:message code="Name"/>:
                            <form:input path="name"/>
                        </form:label>
                        <form:errors path="name" cssClass="error" element="p"/>
                    </div>
                    <div class="flex-grow-1">
                        <form:label path="surname"><spring:message code="Surname"/>:
                            <form:input path="surname"/>
                        </form:label>
                        <form:errors path="surname" cssClass="error" element="p"/>
                    </div>
                </div>

                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2">
                        <form:label path="email"><spring:message code="Email"/>:
                            <form:input path="email"/>
                        </form:label>
                        <form:errors path="email" cssClass="error" element="p"/>
                    </div>
                    <div class="flex-grow-1">
                        <form:label path="neighborhood"><spring:message code="Neighborhood"/>:
                            <form:input path="neighborhood"/>
                        </form:label>
                        <form:errors path="neighborhood" cssClass="error" element="p"/>
                    </div>
                </div>

                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2" style="width: 70%;">
                        <form:label path="subject"><spring:message code="Subject"/>:
                            <form:input path="subject"/>
                        </form:label>
                        <form:errors path="subject" cssClass="error" element="p"/>
                    </div>
                    <div>
                        <form:label path="channel">
                            <spring:message code="Channel"/>:
                        </form:label>
                        <form:select path="channel">
                            <c:forEach var="entry" items="${channelList}">
                                <form:option value="${entry.value.getChannelId()}">
                                    ${entry.key}
                                </form:option>
                            </c:forEach>
                        </form:select>
                    </div>
                        <%--                    <div class="flex-grow-1" style="width: 20%;">--%>
                        <%--                        <label for="tags" class="form-label">--%>
                        <%--                            <i class="fas fa-flag text-danger"></i> Etiqueta--%>
                        <%--                        </label>--%>
                        <%--                        <select class="form-control" id="tags" name="tags">--%>
                        <%--                            <option value="security">Seguridad</option>--%>
                        <%--                            <option value="lost">Administrativo</option>--%>
                        <%--                            <option value="administrative">Perdida</option>--%>
                        <%--                            <option value="administrative">Servicio</option>--%>
                        <%--                            <option value="administrative">Evento</option>--%>
                        <%--                            <option value="administrative">Deporte</option>--%>
                        <%--                        </select>--%>
                        <%--                    </div>--%>
                </div>
                <div class="form-group">
                    <form:label path="message"><spring:message code="Message"/>:
                        <form:input path="message"/>
                    </form:label>
                    <form:errors path="message" cssClass="error" element="p"/>
                </div>



                <div class="mb-3">
                    <form:label path="imageFile" class="form-label"><spring:message code="Image"/>:</form:label>
                    <form:input type="file" path="imageFile" class="form-control" onchange="preview()"/>
                </div>

                <div class="container col-md-6">
                    <img id="frame" src="" class="img-fluid"  alt="uploading image"/>
                </div>




                <div class="tags-input">
                    <h2>Tags Input</h2>
                    <label for="tag-input1">
                        <input type="text" id="tag-input1" >
                    </label>
                    <small class="text-muted">You can enter up to 5 tags.</small>
                </div>

                <form:label path="tags">
                    <form:input type="hidden"  name="tags" id="tags-input" value="" path="tags"/>
                </form:label>

                <script>
                    let tagsString;

                    function submitForm() {
                        clearImage();
                        // Get the tags entered by the user into an array (replace this with your logic)
                        // Get the tags from tagInput1 and convert them to a string
                        const tagsArray = tagInput1.arr; // Assuming tagInput1 has an 'arr' property with the tags

                        // Convert the tagsArray to a string
                        tagsString = tagsArray.join(',');
                        tagsString += ',Admin';
                        // Update the hidden input field's value with the tags as a comma-separated string
                        document.getElementById('tags-input').value = tagsString;
                        //document.getElementById('submit-publishForm-button').submit();
                    }
                </script>

                <div class="d-flex justify-content-end">
                    <button onclick="submitForm()" type="submit" class="btn btn-primary custom-btn"><spring:message code="Post.verb"/></button>
                </div>

                <script>
                    (function(){

                        "use strict"


                        // Plugin Constructor
                        var TagsInput = function(opts){
                            this.options = Object.assign(TagsInput.defaults , opts);
                            this.init();
                        }

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

                            if (this.arr.length >= 5) {
                                console.log('You can only enter up to 5 tags.');
                                return;
                            }

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
                            var plugin = this;

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
                            tags.wrapper.addEventListener('click' ,function(){
                                tags.input.focus();
                            });


                            tags.input.addEventListener('keydown' , function(e){
                                var str = tags.input.value.trim();

                                if( !!(~[9 , 13 , 188].indexOf( e.keyCode ))  )
                                {
                                    e.preventDefault();
                                    tags.input.value = "";
                                    if(str != "")
                                        tags.addTag(str);
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

                    var tagInput1 = new TagsInput({
                        selector: 'tag-input1',
                        duplicate : false,
                        max : 10
                    });
                    tagInput1.addData(["pruebas", "sacame"])

                    function preview() {
                        frame.src = URL.createObjectURL(event.target.files[0]);
                    }
                    function clearImage() {
                        document.getElementById('frame').value = null;
                        frame.src = "";
                    }


                </script>

            </form:form>

        </div>
    </div>

    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/" class="btn btn-link volver-btn"><spring:message code="GoBackToMainPage"/></a>
    </div>
</div>

<!-- Bootstrap JS and jQuery -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>

</body>
</html>
