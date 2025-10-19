# Smart Health Companion

A comprehensive Spring Boot web application for health management with symptom checking, first aid guidance, health tracking, and emergency SOS features.

## 🚀 Features

### Core Modules
- **Symptom Checker** - AI-powered symptom analysis with recommendations
- **First Aid Guidance** - Step-by-step emergency procedures and instructions
- **Health Tracker** - Track vitals, appointments, medications, and health records
- **Nearby Services** - Find hospitals and pharmacies using Google Places API
- **SOS Emergency** - Emergency alert system with location tracking
- **Admin Panel** - User management, SOS logs, and contact message handling
- **Contact System** - Email integration for contact form submissions

### Technical Features
- **Spring Security** - Role-based authentication and authorization
- **JPA/Hibernate** - Database ORM with MySQL integration
- **Thymeleaf** - Server-side templating with Bootstrap 5 UI
- **Email Integration** - SMTP email service for notifications
- **RESTful APIs** - Clean API endpoints for all functionality
- **Responsive Design** - Mobile-friendly Bootstrap 5 interface

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.0
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript, Chart.js
- **Database**: MySQL 8.0
- **Security**: Spring Security with BCrypt
- **Email**: Spring Mail with SMTP
- **Build Tool**: Maven
- **Deployment**: Render.com / Railway.app ready

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd smart-health-companion
```

### 2. Database Setup

#### Option A: Local MySQL
```sql
CREATE DATABASE smart_health_companion;
CREATE USER 'smarthealth'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON smart_health_companion.* TO 'smarthealth'@'localhost';
FLUSH PRIVILEGES;
```

#### Option B: Railway MySQL (Recommended for deployment)
1. Go to [Railway.app](https://railway.app)
2. Create a new project
3. Add MySQL database service
4. Copy the connection details

### 3. Configuration

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://your-mysql-host:port/database_name
spring.datasource.username=your_username
spring.datasource.password=your_password

# Email Configuration (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-gmail@gmail.com
spring.mail.password=your-app-password

# Google Places API
google.places.api.key=your-google-places-api-key
```

### 4. Gmail Setup (for email functionality)

1. Enable 2-factor authentication on your Gmail account
2. Generate an App Password:
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate password for "Mail"
3. Use this app password in `application.properties`

### 5. Google Places API Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Enable Places API
3. Create API key
4. Restrict API key to Places API
5. Add your domain to allowed origins

### 6. Build and Run

```bash
# Build the application
mvn clean package

# Run the application
mvn spring-boot:run

# Or run the JAR file
java -jar target/smart-health-companion-0.0.1-SNAPSHOT.jar
```

### 7. Access the Application

- **Application**: http://localhost:8080
- **Admin Panel**: http://localhost:8080/admin/dashboard

## 👤 Default Admin Credentials

- **Email**: admin@smarthealth.com
- **Password**: admin123

*Note: Change these credentials after first login*

## 📱 API Endpoints

### Public Endpoints
- `GET /` - Home page
- `GET /login` - Login page
- `GET /register` - Registration page
- `POST /register` - Process registration
- `GET /contact` - Contact form
- `POST /contact/submit` - Submit contact form

### User Endpoints (Authentication Required)
- `GET /dashboard` - User dashboard
- `GET /health-tracker` - Health tracking
- `POST /health-tracker/add` - Add health record
- `GET /symptom-checker` - Symptom checker
- `POST /symptom-checker/check` - Process symptoms
- `GET /first-aid` - First aid guidance
- `GET /nearby-services` - Find nearby services
- `GET /sos` - SOS emergency
- `POST /sos/emergency` - Send SOS alert

### Admin Endpoints (Admin Role Required)
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/users` - User management
- `GET /admin/sos-logs` - SOS logs management
- `GET /admin/messages` - Contact messages
- `POST /admin/users/delete/{id}` - Delete user
- `POST /admin/sos-logs/update-status/{id}` - Update SOS status

## 🗄️ Database Schema

### Tables
- **users** - User accounts and profiles
- **health_records** - Health tracking data
- **sos_logs** - Emergency SOS records
- **contact_messages** - Contact form submissions

### Sample Data
The application includes demo data for testing:
- Sample users (admin and regular users)
- Sample health records
- Sample SOS logs
- Sample contact messages

## 🚀 Deployment

### Deploy to Render.com

1. **Push to GitHub**
```bash
git add .
git commit -m "Initial commit"
git push origin main
```

2. **Create Render Service**
   - Go to [Render.com](https://render.com)
   - Connect your GitHub repository
   - Create new Web Service
   - Configure:
     - **Build Command**: `./mvnw clean package -DskipTests`
     - **Start Command**: `java -jar target/smart-health-companion-0.0.1-SNAPSHOT.jar`
     - **Environment**: Java

3. **Environment Variables**
   - `SPRING_DATASOURCE_URL` - Database URL
   - `SPRING_DATASOURCE_USERNAME` - Database username
   - `SPRING_DATASOURCE_PASSWORD` - Database password
   - `SPRING_MAIL_USERNAME` - Gmail username
   - `SPRING_MAIL_PASSWORD` - Gmail app password
   - `GOOGLE_PLACES_API_KEY` - Google Places API key

### Deploy to Railway.app

1. **Install Railway CLI**
```bash
npm install -g @railway/cli
```

2. **Deploy**
```bash
railway login
railway init
railway up
```

3. **Configure Environment Variables**
```bash
railway variables set SPRING_DATASOURCE_URL=your-db-url
railway variables set SPRING_MAIL_USERNAME=your-gmail
# ... other variables
```

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Manual Testing Checklist

#### User Registration & Login
- [ ] Register new user
- [ ] Login with credentials
- [ ] Access dashboard
- [ ] Logout functionality

#### Health Tracking
- [ ] Add health record
- [ ] Edit health record
- [ ] Delete health record
- [ ] View health statistics

#### Symptom Checker
- [ ] Select symptoms
- [ ] Get recommendations
- [ ] View results

#### First Aid
- [ ] Browse first aid types
- [ ] View step-by-step instructions
- [ ] Search functionality

#### Nearby Services
- [ ] Enter location
- [ ] Find hospitals
- [ ] Find pharmacies
- [ ] View service details

#### SOS Emergency
- [ ] Send SOS alert
- [ ] View SOS logs
- [ ] Update SOS status

#### Admin Panel
- [ ] Access admin dashboard
- [ ] Manage users
- [ ] View SOS logs
- [ ] Handle contact messages

## 🔧 Configuration Options

### Application Properties
```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Database Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Email Configuration
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=admin123
spring.security.user.roles=ADMIN
```

### Customization
- Modify `symptoms.json` for symptom data
- Update `firstaid.json` for first aid procedures
- Customize CSS in `static/css/style.css`
- Add JavaScript functionality in `static/js/main.js`

## 🐛 Troubleshooting

### Common Issues

#### Database Connection Error
```
Error: Could not create connection to database server
```
**Solution**: Check database credentials and ensure MySQL is running

#### Email Not Sending
```
Error: Authentication failed
```
**Solution**: Verify Gmail app password and 2FA settings

#### Google Places API Error
```
Error: This API project is not authorized to use this API
```
**Solution**: Enable Places API and check API key restrictions

#### Port Already in Use
```
Error: Port 8080 was already in use
```
**Solution**: Change port in `application.properties` or stop other services

### Logs
Check application logs for detailed error information:
```bash
tail -f logs/application.log
```

## 📚 Documentation

### Project Structure
```
src/main/java/com/smarthealth/
├── controller/          # REST controllers
├── model/              # JPA entities
├── repository/         # Data access layer
├── service/            # Business logic
├── config/             # Configuration classes
└── SmartHealthApplication.java

src/main/resources/
├── templates/          # Thymeleaf templates
├── static/            # CSS, JS, images
├── data/              # JSON data files
└── application.properties
```

### Key Classes
- `User` - User entity with Spring Security integration
- `HealthRecord` - Health tracking data
- `SOSLog` - Emergency records
- `ContactMessage` - Contact form submissions
- `UserService` - User management service
- `MailService` - Email functionality
- `SOSService` - Emergency handling
- `HealthDataService` - Symptom and first aid data

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- Email: support@smarthealthcompanion.com
- Create an issue on GitHub
- Check the documentation

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Bootstrap team for the UI components
- Chart.js for data visualization
- Font Awesome for icons
- Google for Places API

---

**Smart Health Companion** - Your comprehensive health management platform 🏥💚
