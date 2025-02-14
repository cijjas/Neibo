<div align="center">

[![Neibo App Logo ][repo_logo_img]][page_url]

<div style="position: relative; width: 90%; display: flex; justify-content: center;">

<div style="
position: absolute;
bottom: 10px;
right: 10px;
font-size: 16px;
color: #FAF6E8FF;
">
Your peace of mind, our priority. Stay safe, stay connected.
</div>

</div>


</div>

## Introduction

Welcome to **Neibo**, a full-featured web application designed to streamline and enhance community interactions.
Whether you’re an **admin** managing amenities and user requests, a **service provider** offering services,
or a **resident** engaging with your neighborhood through events, posts, and a marketplace,
Neibo provides a seamless experience tailored to your needs.

## Features

### 🏢 Administration

Admins have full control over the community, with the ability to create and manage amenities, schedule events, curate neighborhood information, and approve or reject residents and service providers. Keep the community well-organized and secure with powerful administration tools.

### 🎾 Amenities Reservation

Easily book community amenities such as tennis courts, pools, and clubhouses. Residents can check availability, make reservations, and manage their bookings hassle-free.

### 📅 Events

Stay updated with community events through an interactive calendar. Join or leave events with a simple click and never miss out on neighborhood gatherings, activities, or important meetings.

### 📢 Announcements & Community Feed

Stay informed with an announcement board for admins and a community feed for residents. Share news, updates, and discussions with neighbors, and even submit complaints or concerns in dedicated channels.

### 🛠️ Service Providers Management

A curated list of trusted neighborhood service providers, complete with ratings and reviews. Residents can easily find and contact plumbers, electricians, landscapers, and more, ensuring quality and reliability in their local community.

### 🌍 Neighborhood Information

Admins can maintain an up-to-date database of important neighborhood information, including contacts, emergency numbers, and essential local services, providing residents with quick access to key resources.

### 🛍️ Marketplace

A community-driven marketplace where residents can buy, sell, and trade goods within the neighborhood. Find great deals, support local sellers, and create a thriving local economy.

## Roles
- **Neighbor**: A neighborhood resident who has access to their neighborhood's resources but cannot modify or create administrative entities.
- **Administrator**: Represents moderation, has access to their neighborhood's resources and can modify or create administrative entities as well as moderate content created by its neighbors.
- **Unverified Neighbor**: A user who has made a request to join a certain neighborhood and must wait to be either accepted (becoming a **Neighbor**) or rejected (becoming a **Rejected**). The neighborhood administrator is in charge of their request. This user only has access to their profile which they can modify.
- **Rejected**: A user whose request to join a certain neighborhood was rejected. They are given the possibility to make a request to another neighborhood. They also have access to their profile.
- **Workers**: Independent workers who offer their services. They make requests to neighborhoods to be verified by them and become listed in those neighborhoods' Service Providers section, thus gaining new potential clients.
- **Super Administrator**: Exercises moderation over all domains beyond neighborhood administrators' scope, for example, moderation over **Workers** and their actions, creation of new neighborhoods, creation of new neighborhood administrators, etc. This role can interact directly with the API but their SPA view is still under construction.

## Test Accounts

| Role                    | Gmail & Neibo Account     | Gmail Account Password  | Neibo Account Password |
|-------------------------|---------------------------|-------------------------|------------------------|
| **Admin**               | moderatorneibo@gmail.com  | moderatorneibo2023b-02  | moderatorneibo         |
| **Verified Neighbor**   | verifiedneibo@gmail.com   | verifiedneibo2023b-02   | verifiedneibo          |
| **Service Provider**    | workerneibo@gmail.com     | workerneibo2023b-02     | workerneibo            |
| **Unverified Neighbor** | unverifiedneibo@gmail.com | unverifiedneibo2023b-02 | unverifiedneibo        |
| **Rejected Neighbor**   | rejectedneibo@gmail.com   | rejectedneibo2023b-02   |                        |

!!!!!!!!!!! Create gmail for rejected and superadmin

## How to Run
### Pre-Built War
1. Clone Project
2. Choose Branch 'api-spa'
3. Run `mvn clean package`, ensuring that Maven and Java 8 are installed on the local machine.
4. Run `docker compose up webapp db`

### In Container Build
1. Clone Project
2. Choose Branch 'api-spa'
3. Run `docker compose up webapp-build db`

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

<!-- Project -->

<!-- Author -->

<!-- Readme links -->

<!-- Other projects links -->
