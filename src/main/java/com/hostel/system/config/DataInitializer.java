package com.hostel.system.config;

import com.hostel.system.builder.StudentBuilder;
import com.hostel.system.factory.UserFactory;
import com.hostel.system.model.*;
import com.hostel.system.model.enums.ComplaintStatus;
import com.hostel.system.model.enums.FeeStatus;
import com.hostel.system.model.enums.Role;
import com.hostel.system.model.enums.RoomStatus;
import com.hostel.system.model.enums.RoomType;
import com.hostel.system.repository.*;
import com.hostel.system.singleton.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.logging.Logger;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getLogger(DataInitializer.class.getName());

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;
    private final ComplaintRepository complaintRepository;
    private final FeeRepository feeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                           RoomRepository roomRepository,
                           StudentRepository studentRepository,
                           ComplaintRepository complaintRepository,
                           FeeRepository feeRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
        this.complaintRepository = complaintRepository;
        this.feeRepository = feeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // =========================================================================
        // GANG OF FOUR (GoF) DESIGN PATTERN 1: SINGLETON PATTERN DEMONSTRATION
        // =========================================================================
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        LOGGER.info(">>> " + dbConnection.getConnectionStatus());

        if (userRepository.count() > 0) {
            LOGGER.info("Database already initialized with pre-existing records.");
            return;
        }

        LOGGER.info("Initializing Database with Sample Data...");

        // 1. Seed Rooms
        Room room101 = roomRepository.save(new Room("A-101", 1, 2, RoomType.DOUBLE, RoomStatus.AVAILABLE));
        Room room102 = roomRepository.save(new Room("A-102", 1, 2, RoomType.DOUBLE, RoomStatus.AVAILABLE));
        Room room201 = roomRepository.save(new Room("B-201", 2, 1, RoomType.SINGLE, RoomStatus.AVAILABLE));
        Room room202 = roomRepository.save(new Room("B-202", 2, 3, RoomType.TRIPLE, RoomStatus.MAINTENANCE));

        // =========================================================================
        // GANG OF FOUR (GoF) DESIGN PATTERN 2: FACTORY METHOD PATTERN DEMONSTRATION
        // UserFactory creates polymorphic Admin and Warden instances
        // =========================================================================
        User adminUser = UserFactory.createUser(Role.ROLE_ADMIN, "admin", passwordEncoder.encode("admin123"), "Chief Admin", "admin@hostel.com", "9876543210");
        userRepository.save(adminUser);
        System.out.println("Hash : " + adminUser.getPassword());

        System.out.println(
        "Matches : " +
        passwordEncoder.matches(
                "admin123",
                adminUser.getPassword()
            )
        );
        User wardenUser = UserFactory.createUser(Role.ROLE_WARDEN, "warden", passwordEncoder.encode("warden123"), "Dr. Robert Warden", "warden@hostel.com", "9876543211");
        userRepository.save(wardenUser);

        // =========================================================================
        // GANG OF FOUR (GoF) DESIGN PATTERN 3: BUILDER PATTERN DEMONSTRATION
        // StudentBuilder fluidly constructs Student objects
        // =========================================================================
        Student student1 = new StudentBuilder()
                .setUsername("student1")
                .setPassword(passwordEncoder.encode("student123"))
                .setName("Alice Smith")
                .setEmail("alice@student.com")
                .setPhone("9876543212")
                .setRegistrationNumber("2026-CSE-001")
                .setDepartment("Computer Science")
                .setYear(3)
                .setGender("Female")
                .setAddress("123 Tech Park, City")
                .setParentName("John Smith")
                .setParentPhone("9123456789")
                .setBloodGroup("O+")
                .setMedicalCondition("None")
                .setRoom(room101)
                .build();
        
        studentRepository.save(student1);
        room101.setOccupiedCount(room101.getOccupiedCount() + 1);
        room101.updateStatus();
        roomRepository.save(room101);

        Student student2 = new StudentBuilder()
                .setUsername("student2")
                .setPassword(passwordEncoder.encode("student123"))
                .setName("Bob Johnson")
                .setEmail("bob@student.com")
                .setPhone("9876543213")
                .setRegistrationNumber("2026-ECE-002")
                .setDepartment("Electronics")
                .setYear(2)
                .setGender("Male")
                .setAddress("456 Signal St, City")
                .setParentName("Mark Johnson")
                .setParentPhone("9123456790")
                .setBloodGroup("A+")
                .setMedicalCondition("Dust Allergy")
                .setRoom(room201)
                .build();

        studentRepository.save(student2);
        room201.setOccupiedCount(room201.getOccupiedCount() + 1);
        room201.updateStatus();
        roomRepository.save(room201);

        // 4. Seed Complaints
        Complaint c1 = new Complaint(student1, "Air Conditioner Noise", "The AC unit in room A-101 makes loud vibrating noises during night hours.");
        complaintRepository.save(c1);

        Complaint c2 = new Complaint(student2, "Plumbing Leakage in Bathroom", "Water leaking under the washbasin tap.");
        c2.setStatus(ComplaintStatus.RESOLVED);
        c2.setResolutionNote("Replaced washbasin washer and sealed pipe joints.");
        complaintRepository.save(c2);

        // 5. Seed Fees
        Fee f1 = new Fee(student1, new BigDecimal("25000.00"), LocalDate.now().plusDays(25), FeeStatus.UNPAID);
        feeRepository.save(f1);

        Fee f2 = new Fee(student2, new BigDecimal("30000.00"), LocalDate.now().minusDays(10), FeeStatus.PAID);
        f2.setPaymentDate(LocalDate.now().minusDays(12));
        f2.setTransactionRef("TXN-984729104");
        feeRepository.save(f2);

        LOGGER.info("Sample Data Initialization Completed Successfully.");
    }
}
