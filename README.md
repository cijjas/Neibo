<div align="center">

[![Neibo App Logo][repo_logo_img]][page_url]

</div>

# Neibo: Empowering Neighborhoods & Communities

**Neibo** is a comprehensive solution designed to enhance connectivity within gated communities. By centralizing communication and service management, it enables residents and administrators to interact seamlessly and efficiently.

Residents can stay informed through an interactive feed that includes 'hot' and 'trending' posts, ensuring they never miss important updates. Neibo also simplifies everyday tasks such as reserving amenities and signing up for events. Administrators can manage everything from facility hours to event details, while residents participate effortlessly.

One of Neibo’s key strengths lies in its robust service providers section. Residents can browse local professionals—ranging from electricians to yoga instructors—read reviews, and make confident hiring decisions. Meanwhile, service providers can expand their reach across multiple communities through personalized dashboards that highlight their offerings.

Explore these features and more by logging in with the test credentials provided below. Discover how Neibo transforms community engagement into a more connected, efficient, and secure experience.

---

## Introduction

[![Neibo Presentation][neibo_presentation]][page_url]

Neibo brings together advanced administrative tools, real-time event management, and a dynamic marketplace in a single platform. Our goal is to create smart, connected neighborhoods that foster collaboration and sustainable growth.

---

## Key Features

### 🏢 Administration
- **Community Management:** Administrators have full control of community settings, amenities, events, and resident data.
- **Security and Moderation:** Ensure a safe environment by approving or rejecting new residents and service providers.

### 🎾 Amenities Reservation
- **Simple Booking:** Residents can view availability and reserve shared resources, such as sports courts or clubhouses, with just a few clicks.

### 📅 Events
- **Interactive Calendar:** Stay up-to-date on upcoming events. Residents can easily join community gatherings and activities.

### 📢 Announcements & Community Feed
- **Important Updates:** Administrators post announcements while residents share news through a real-time feed.
- **Engagement and Feedback:** Encourage participation with likes, comments, and discussions.

### 🛠️ Service Providers Management
- **Verified Professionals:** Residents can find and review trusted local service providers.
- **Business Growth:** Providers expand their reach across different neighborhoods, benefiting both communities and service professionals.

### 🌍 Neighborhood Information
- **Centralized Resources:** Quick access to essential contacts, emergency numbers, and community guidelines.

### 🛍️ Marketplace
- **Local Commerce:** A dedicated marketplace for buying, selling, or trading items, helping foster a thriving local economy.

---

## User Roles & Access Levels

Each role is designed to maintain security and a positive user experience:

- **Neighbor**  
  Regular community member with access to neighborhood resources and social features.

- **Administrator**  
  Has comprehensive control over administrative settings, event planning, and user moderation.

- **Unverified Neighbor**  
  User requesting neighborhood membership, awaiting administrator approval.

- **Rejected Neighbor**  
  User whose membership request has been declined but retains the ability to reapply elsewhere.

- **Worker**  
  Service provider seeking verification to appear in the community’s Service Providers directory.

- **Super Administrator**  
  Oversees all neighborhoods, manages platform-wide settings, and handles advanced moderation. *(SPA view for this role is under development.)*

---

## Test Accounts

Use the following credentials for a hands-on demo of Neibo:

| **Role**                | **Gmail & Neibo Account**    | **Gmail Password**           | **Neibo Password**  |
|-------------------------|------------------------------|------------------------------|---------------------|
| **Admin**               | moderatorneibo@gmail.com     | moderatorneibo2023b-02       | moderatorneibo      |
| **Verified Neighbor**   | verifiedneibo@gmail.com      | verifiedneibo2023b-02        | verifiedneibo       |
| **Service Provider**    | workerneibo@gmail.com        | workerneibo2023b-02          | workerneibo         |
| **Unverified Neighbor** | unverifiedneibo@gmail.com    | unverifiedneibo2023b-02      | unverifiedneibo     |
| **Rejected Neighbor**   | rejectedneibo@gmail.com      | rejectedneibo2023b-02        | rejectedneibo       |
| **Super Administrator** | supermoderatorneibo@gmail.com    | supermoderatorneibo2023b-02      | supermoderatorneibo     |

---


## Running the Application

### 🚀 Option 1: Using a Pre-Built WAR
1. **Clone the repository**:
   ```sh
   git clone https://bitbucket.org/itba/paw-2023b-02/
   cd paw-2023b-02
   ```
   
2. **Switch to the appropriate branch**:
   ```sh
   git checkout api-spa
   ``` 
   

3. **Build the project** (Ensure Maven and Java 8 are installed):
   ```sh
   mvn clean package
   ```  
   
4. **Start the application using Docker Compose**:
   ```sh
   docker compose up webapp db
   ```  
5. **Access the application by visiting:** [http://localhost:8080/paw-2023b-02/](http://localhost:8080/paw-2023b-02/)

### 🐳 Option 2: Building Inside a Container
1. **Clone the repository**:
   ```sh
   git clone https://bitbucket.org/itba/paw-2023b-02/
   cd paw-2023b-02
   ```  
   
2. **Switch to the appropriate branch**:
   ```sh
   git checkout api-spa
   ```  
   
3. **Start the application and build inside the container**:
   ```sh
   docker compose up webapp-build db
   ```
4. **Access the application by visiting:** [http://localhost:8080/paw-2023b-02/](http://localhost:8080/paw-2023b-02/)

---

<h3 style="width: 100%; display: flex; flex-direction: row; justify-content: end; align-items: center;"> Contributors</h3>

<table style="width: 100%; display: flex; flex-direction: row; justify-content: end; align-items: center;">
<tr>
<td align="center">
<a href="https://github.com/cijjas">
<img src="https://avatars.githubusercontent.com/u/95446446?v=4" width="50px;" alt="Chris"/>
<br /><sub><b>Chris</b></sub>
</a>
</td>
<td align="center">
<a href="https://github.com/JoacoGirod">
<img src="https://avatars.githubusercontent.com/u/62113898?v=4" width="50px;" alt="Joaco"/>
<br /><sub><b>Joaco</b></sub>
</a>
</td>
<td align="center">
<a href="https://github.com/flopezmenardi">
<img src="https://avatars.githubusercontent.com/u/95313072?v=4" width="50px;" alt="Felix"/>
<br /><sub><b>Felix</b></sub>
</a>
</td>
<td align="center">
<a href="https://github.com/meursault00">
<img src="https://avatars.githubusercontent.com/u/95638674?v=4" width="50px;" alt="Inaki"/>
<br /><sub><b>Inaki</b></sub>
</a>
</td>
</tr>
</table>


<!-- Go -->

<!-- Repository -->

[page_url]: http://old-pawserver.it.itba.edu.ar/paw-2023b-02
[repo_logo_img]: /frontend/src/assets/images/banner_neibo.png
[neibo_presentation]: /frontend/src/assets/images/v2.png

<!-- Project -->

<!-- Author -->

<!-- Readme links -->

<!-- Other projects links -->
