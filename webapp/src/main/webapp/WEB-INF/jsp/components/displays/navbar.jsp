<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/components/structure/tooltips.jsp" %>

<div class="nav-container">
    <nav class="navbar navbar-expand-lg custom-navbar">
        <div class="container-xl">
            <div class="d-flex justify-content-between align-items-center w-100">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                    <svg class="svg-logo" width="55" height="54" viewBox="0 0 711 676" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M277 516.5C173.471 510.748 88.3725 601.154 52.731 653.054C50.7792 655.896 46.266 653.887 47.3215 650.605C79.3442 551.024 177.991 468.147 237.5 463C330 455 349.786 487.698 508 495C610.89 499.749 667.55 468.655 702.824 435.796C705.542 433.265 712.003 437.933 710.028 441.079C684.305 482.063 637.028 535 537 535C382.571 535 430 525 277 516.5Z" fill="var(--background)"></path>
                        <path d="M328 606.663C263.473 600.941 199.637 639.517 165.188 675.03C162.619 677.679 155.151 673.473 156.822 670.182C188.039 608.686 243.758 574.645 282.5 570.163C343 563.163 346.53 573.547 459.5 595.663C541.067 611.631 567.503 606.738 592.502 587.738C595.135 585.737 598.924 588.691 597.277 591.558C583.142 616.169 558.748 639.256 478 629.163C367.722 615.378 429.5 615.663 328 606.663Z" fill="var(--background)"></path>
                        <path d="M253.5 431.5C146.488 414.348 41.0106 525.913 5.59293 571.638C3.71802 574.059 -0.226579 572.493 0.128361 569.452C14.4897 446.405 82.4602 391.028 166 375C252 358.5 300.124 400.346 458 413C558.445 421.051 642.722 409.505 690.997 385.574C694.335 383.919 701.764 391.441 699.183 394.127C666.042 428.622 609.573 458.5 508 458.5C353.571 458.5 397 454.5 253.5 431.5Z" fill="var(--background)"></path>
                        <path d="M546.671 228.508C514.271 302.108 520.944 388.508 525.277 422.508C509.277 422.508 471.277 429.508 401.171 415.508C279.277 386.508 269.777 369.508 157.671 375.008C150.52 375.359 155.671 334.508 157.671 309.008C159.671 283.508 182.171 254.508 207.671 228.508C233.171 202.508 278.171 183.008 308.671 178.508C339.171 174.008 350.171 119.008 365.671 88.008C381.171 57.008 481.326 5.85161 535.171 30.508C601.777 61.008 653.171 99.008 667.671 112.508C682.171 126.008 676.171 140.508 662.671 183.008C651.871 217.008 638.504 227.508 633.171 228.508C590.771 240.108 557.837 233.341 546.671 228.508Z" fill="var(--background)"></path>
                        <ellipse cx="424.428" cy="47.3431" rx="36.5" ry="32.5" transform="rotate(33.9471 424.428 47.3431)" fill="var(--background)"></ellipse>
                    </svg>
                    neibo
                </a>


                <%@ include file="/WEB-INF/jsp/components/widgets/userProfileWidget.jsp" %>
            </div>
        </div>
    </nav>



    <div class="wave">
        <svg class="w-100" preserveAspectRatio="none" width="1200" height="382" viewBox="0 0 1200 382" fill="none" xmlns="http://www.w3.org/2000/svg">
            <g filter="url(#filter0_d_277_3)">
                <path d="M321.39 222.002C379.39 205.635 435.55 176.299 493.39 158.506C575.78 133.144 661.58 131.612 743.84 157.914C823.78 183.413 906.67 245.604 985.66 277.201C1055.71 305.233 1132.19 316.776 1200 281.752V0H0V177.876C102.466 236.813 213.613 252.073 321.39 222.002Z" fill="var(--primary)"/>
                <path d="M321.39 222.002C379.39 205.635 435.55 176.299 493.39 158.506C575.78 133.144 661.58 131.612 743.84 157.914C823.78 183.413 906.67 245.604 985.66 277.201C1055.71 305.233 1132.19 316.776 1200 281.752V0H0V177.876C102.466 236.813 213.613 252.073 321.39 222.002Z" fill="url(#paint0_radial_277_3)" fill-opacity="0.2"/>
                <path d="M321.39 222.002C379.39 205.635 435.55 176.299 493.39 158.506C575.78 133.144 661.58 131.612 743.84 157.914C823.78 183.413 906.67 245.604 985.66 277.201C1055.71 305.233 1132.19 316.776 1200 281.752V0H0V177.876C102.466 236.813 213.613 252.073 321.39 222.002Z" fill="url(#paint1_radial_277_3)" fill-opacity="0.2"/>
            </g>
            <defs>
                <filter id="filter0_d_277_3" x="-32" y="-12" width="1290" height="393.5" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
                    <feFlood flood-opacity="0" result="BackgroundImageFix"/>
                    <feColorMatrix in="SourceAlpha" type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"/>
                    <feOffset dx="13" dy="33"/>
                    <feGaussianBlur stdDeviation="22.5"/>
                    <feComposite in2="hardAlpha" operator="out"/>
                    <feColorMatrix type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.25 0"/>
                    <feBlend mode="normal" in2="BackgroundImageFix" result="effect1_dropShadow_277_3"/>
                    <feBlend mode="normal" in="SourceGraphic" in2="effect1_dropShadow_277_3" result="shape"/>
                </filter>
                <radialGradient id="paint0_radial_277_3" cx="0" cy="0" r="1" gradientUnits="userSpaceOnUse" gradientTransform="translate(1200 266) rotate(-128.273) scale(347.104 789.416)">
                    <stop stop-color="#0D2202"/>
                    <stop offset="0.747285" stop-opacity="0"/>
                    <stop offset="1" stop-color="#52CB19" stop-opacity="0"/>
                </radialGradient>
                <radialGradient id="paint1_radial_277_3" cx="0" cy="0" r="1" gradientUnits="userSpaceOnUse" gradientTransform="translate(364 249) rotate(-100.767) scale(310.466 706.089)">
                    <stop stop-color="#053407"/>
                    <stop offset="0.747285" stop-opacity="0"/>
                    <stop offset="1" stop-opacity="0"/>
                </radialGradient>
            </defs>
        </svg>







    </div>
</div>

