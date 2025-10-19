package com.smarthealth.service;

import com.smarthealth.model.ContactMessage;
import com.smarthealth.model.HealthRecord;
import com.smarthealth.model.SOSLog;
import com.smarthealth.model.User;
import com.smarthealth.repository.ContactMessageRepository;
import com.smarthealth.repository.HealthRecordRepository;
import com.smarthealth.repository.SOSLogRepository;
import com.smarthealth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Data Initialization Service
 * Creates demo data for testing and development
 */
@Service
@Transactional
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private SOSLogRepository sosLogRepository;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize data if no users exist
        if (userRepository.count() == 0) {
            initializeDemoData();
        }
    }

    private void initializeDemoData() {
        System.out.println("Initializing demo data...");

        // Create admin user
        User admin = createAdminUser();
        userRepository.save(admin);

        // Create regular users
        List<User> users = createRegularUsers();
        userRepository.saveAll(users);

        // Create health records
        createHealthRecords(admin, users);

        // Create SOS logs
        createSOSLogs(users);

        // Create contact messages
        createContactMessages();

        System.out.println("Demo data initialization completed!");
        System.out.println("Admin credentials: admin@smarthealth.com / admin123");
    }

    private User createAdminUser() {
        User admin = new User();
        admin.setName("System Administrator");
        admin.setEmail("admin@smarthealth.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.ADMIN);
        admin.setCreatedAt(LocalDateTime.now().minusDays(30));
        admin.setLastLogin(LocalDateTime.now().minusHours(2));
        admin.setIsActive(true);
        return admin;
    }

    private List<User> createRegularUsers() {
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPassword(passwordEncoder.encode("password123"));
        user1.setRole(User.Role.USER);
        user1.setCreatedAt(LocalDateTime.now().minusDays(15));
        user1.setLastLogin(LocalDateTime.now().minusHours(1));
        user1.setIsActive(true);

        User user2 = new User();
        user2.setName("Jane Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setPassword(passwordEncoder.encode("password123"));
        user2.setRole(User.Role.USER);
        user2.setCreatedAt(LocalDateTime.now().minusDays(10));
        user2.setLastLogin(LocalDateTime.now().minusMinutes(30));
        user2.setIsActive(true);

        User user3 = new User();
        user3.setName("Mike Johnson");
        user3.setEmail("mike.johnson@example.com");
        user3.setPassword(passwordEncoder.encode("password123"));
        user3.setRole(User.Role.USER);
        user3.setCreatedAt(LocalDateTime.now().minusDays(5));
        user3.setLastLogin(LocalDateTime.now().minusDays(1));
        user3.setIsActive(true);

        User user4 = new User();
        user4.setName("Sarah Wilson");
        user4.setEmail("sarah.wilson@example.com");
        user4.setPassword(passwordEncoder.encode("password123"));
        user4.setRole(User.Role.USER);
        user4.setCreatedAt(LocalDateTime.now().minusDays(3));
        user4.setLastLogin(LocalDateTime.now().minusHours(6));
        user4.setIsActive(false); // Inactive user for testing

        return Arrays.asList(user1, user2, user3, user4);
    }

    private void createHealthRecords(User admin, List<User> users) {
        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);

        // Health records for user1
        HealthRecord record1 = new HealthRecord();
        record1.setUser(user1);
        record1.setType(HealthRecord.RecordType.BLOOD_PRESSURE);
        record1.setValue("120/80");
        record1.setNotes("Normal reading");
        record1.setRecordDate(LocalDateTime.now().minusDays(1));
        record1.setCreatedAt(LocalDateTime.now().minusDays(1));

        HealthRecord record2 = new HealthRecord();
        record2.setUser(user1);
        record2.setType(HealthRecord.RecordType.HEART_RATE);
        record2.setValue("72 bpm");
        record2.setNotes("Resting heart rate");
        record2.setRecordDate(LocalDateTime.now().minusDays(2));
        record2.setCreatedAt(LocalDateTime.now().minusDays(2));

        HealthRecord record3 = new HealthRecord();
        record3.setUser(user1);
        record3.setType(HealthRecord.RecordType.WEIGHT);
        record3.setValue("70 kg");
        record3.setNotes("Morning weight");
        record3.setRecordDate(LocalDateTime.now().minusDays(3));
        record3.setCreatedAt(LocalDateTime.now().minusDays(3));

        HealthRecord record4 = new HealthRecord();
        record4.setUser(user1);
        record4.setType(HealthRecord.RecordType.APPOINTMENT);
        record4.setValue("Annual Checkup");
        record4.setNotes("Dr. Smith - General Medicine");
        record4.setRecordDate(LocalDateTime.now().plusDays(7));
        record4.setCreatedAt(LocalDateTime.now().minusDays(5));

        // Health records for user2
        HealthRecord record5 = new HealthRecord();
        record5.setUser(user2);
        record5.setType(HealthRecord.RecordType.TEMPERATURE);
        record5.setValue("98.6°F");
        record5.setNotes("Normal body temperature");
        record5.setRecordDate(LocalDateTime.now().minusHours(6));
        record5.setCreatedAt(LocalDateTime.now().minusHours(6));

        HealthRecord record6 = new HealthRecord();
        record6.setUser(user2);
        record6.setType(HealthRecord.RecordType.BLOOD_SUGAR);
        record6.setValue("95 mg/dL");
        record6.setNotes("Fasting glucose");
        record6.setRecordDate(LocalDateTime.now().minusDays(1));
        record6.setCreatedAt(LocalDateTime.now().minusDays(1));

        HealthRecord record7 = new HealthRecord();
        record7.setUser(user2);
        record7.setType(HealthRecord.RecordType.MEDICATION);
        record7.setValue("Vitamin D 1000 IU");
        record7.setNotes("Daily supplement");
        record7.setRecordDate(LocalDateTime.now());
        record7.setCreatedAt(LocalDateTime.now());

        // Health records for user3
        HealthRecord record8 = new HealthRecord();
        record8.setUser(user3);
        record8.setType(HealthRecord.RecordType.SYMPTOM);
        record8.setValue("Headache, Fatigue");
        record8.setNotes("Mild headache after long day");
        record8.setRecordDate(LocalDateTime.now().minusHours(12));
        record8.setCreatedAt(LocalDateTime.now().minusHours(12));

        HealthRecord record9 = new HealthRecord();
        record9.setUser(user3);
        record9.setType(HealthRecord.RecordType.APPOINTMENT);
        record9.setValue("Dental Cleaning");
        record9.setNotes("Dr. Brown - Dentistry");
        record9.setRecordDate(LocalDateTime.now().plusDays(14));
        record9.setCreatedAt(LocalDateTime.now().minusDays(2));

        List<HealthRecord> healthRecords = Arrays.asList(
            record1, record2, record3, record4, record5, record6, record7, record8, record9
        );

        healthRecordRepository.saveAll(healthRecords);
    }

    private void createSOSLogs(List<User> users) {
        User user1 = users.get(0);
        User user2 = users.get(1);

        SOSLog sos1 = new SOSLog();
        sos1.setUser(user1);
        sos1.setLocation("123 Main Street, City, State");
        sos1.setPhoneNumber("+1234567890");
        sos1.setEmergencyType("Medical Emergency");
        sos1.setDescription("Chest pain and shortness of breath");
        sos1.setStatus(SOSLog.EmergencyStatus.RESOLVED);
        sos1.setCreatedAt(LocalDateTime.now().minusDays(5));
        sos1.setResolvedAt(LocalDateTime.now().minusDays(5).plusHours(2));

        SOSLog sos2 = new SOSLog();
        sos2.setUser(user2);
        sos2.setLocation("456 Oak Avenue, City, State");
        sos2.setPhoneNumber("+1234567891");
        sos2.setEmergencyType("Accident");
        sos2.setDescription("Minor car accident, no injuries");
        sos2.setStatus(SOSLog.EmergencyStatus.PENDING);
        sos2.setCreatedAt(LocalDateTime.now().minusHours(3));

        SOSLog sos3 = new SOSLog();
        sos3.setUser(user1);
        sos3.setLocation("789 Pine Road, City, State");
        sos3.setPhoneNumber("+1234567890");
        sos3.setEmergencyType("Fire");
        sos3.setDescription("Kitchen fire, smoke inhalation");
        sos3.setStatus(SOSLog.EmergencyStatus.IN_PROGRESS);
        sos3.setCreatedAt(LocalDateTime.now().minusHours(1));

        List<SOSLog> sosLogs = Arrays.asList(sos1, sos2, sos3);
        sosLogRepository.saveAll(sosLogs);
    }

    private void createContactMessages() {
        ContactMessage msg1 = new ContactMessage();
        msg1.setName("Alice Brown");
        msg1.setEmail("alice.brown@example.com");
        msg1.setSubject("Feature Request");
        msg1.setMessage("I would like to request a feature to export health data to PDF format. This would be very helpful for sharing with healthcare providers.");
        msg1.setSentTime(LocalDateTime.now().minusDays(2));
        msg1.setIsRead(true);
        msg1.setIsReplied(true);
        msg1.setAdminReply("Thank you for your suggestion! We are working on PDF export functionality and it will be available in the next update.");
        msg1.setReplyTime(LocalDateTime.now().minusDays(1));

        ContactMessage msg2 = new ContactMessage();
        msg2.setName("Bob Davis");
        msg2.setEmail("bob.davis@example.com");
        msg2.setSubject("Bug Report");
        msg2.setMessage("I'm experiencing an issue with the symptom checker. When I select multiple symptoms, the recommendations don't appear correctly.");
        msg2.setSentTime(LocalDateTime.now().minusHours(6));
        msg2.setIsRead(true);
        msg2.setIsReplied(false);

        ContactMessage msg3 = new ContactMessage();
        msg3.setName("Carol Green");
        msg3.setEmail("carol.green@example.com");
        msg3.setSubject("General Inquiry");
        msg3.setMessage("Is there a mobile app version of Smart Health Companion? I would love to use it on my phone.");
        msg3.setSentTime(LocalDateTime.now().minusHours(2));
        msg3.setIsRead(false);
        msg3.setIsReplied(false);

        ContactMessage msg4 = new ContactMessage();
        msg4.setName("David Lee");
        msg4.setEmail("david.lee@example.com");
        msg4.setSubject("Account Issue");
        msg4.setMessage("I'm having trouble logging into my account. I've tried resetting my password but haven't received the reset email.");
        msg4.setSentTime(LocalDateTime.now().minusMinutes(30));
        msg4.setIsRead(false);
        msg4.setIsReplied(false);

        List<ContactMessage> messages = Arrays.asList(msg1, msg2, msg3, msg4);
        contactMessageRepository.saveAll(messages);
    }
}
