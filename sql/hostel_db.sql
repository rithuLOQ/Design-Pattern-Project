-- =================================================================
-- SMART HOSTEL MANAGEMENT SYSTEM - DATABASE CREATION & SCHEMA SCRIPT
-- Database Name: hostel_db
-- Target Database Engine: MySQL 8.0+
-- =================================================================

CREATE DATABASE IF NOT EXISTS `hostel_db`;
USE `hostel_db`;

-- -----------------------------------------------------------------
-- Table Structure: rooms
-- -----------------------------------------------------------------
DROP TABLE IF EXISTS `complaints`;
DROP TABLE IF EXISTS `fees`;
DROP TABLE IF EXISTS `students`;
DROP TABLE IF EXISTS `wardens`;
DROP TABLE IF EXISTS `admins`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `rooms`;

CREATE TABLE `rooms` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `room_number` VARCHAR(20) NOT NULL UNIQUE,
    `floor` INT NOT NULL,
    `capacity` INT NOT NULL,
    `occupied_count` INT NOT NULL DEFAULT 0,
    `room_type` VARCHAR(20) NOT NULL,
    `status` VARCHAR(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------
-- Table Structure: users (Base table for Joined Inheritance)
-- -----------------------------------------------------------------
CREATE TABLE `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20),
    `role` VARCHAR(20) NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------
-- Table Structure: admins (Extends users)
-- -----------------------------------------------------------------
CREATE TABLE `admins` (
    `id` BIGINT PRIMARY KEY,
    `department_code` VARCHAR(50),
    FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------
-- Table Structure: wardens (Extends users)
-- -----------------------------------------------------------------
CREATE TABLE `wardens` (
    `id` BIGINT PRIMARY KEY,
    `block_assigned` VARCHAR(50),
    FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------
-- Table Structure: students (Extends users)
-- -----------------------------------------------------------------
CREATE TABLE `students` (
    `id` BIGINT PRIMARY KEY,
    `registration_number` VARCHAR(50) NOT NULL UNIQUE,
    `department` VARCHAR(100),
    `year` INT,
    `gender` VARCHAR(10),
    `address` VARCHAR(255),
    `parent_name` VARCHAR(100),
    `parent_phone` VARCHAR(20),
    `blood_group` VARCHAR(10),
    `medical_condition` VARCHAR(255),
    `room_id` BIGINT,
    FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------
-- Table Structure: complaints
-- -----------------------------------------------------------------
CREATE TABLE `complaints` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `title` VARCHAR(150) NOT NULL,
    `description` TEXT NOT NULL,
    `status` VARCHAR(20) NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `resolved_at` DATETIME,
    `resolution_note` TEXT,
    FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------
-- Table Structure: fees
-- -----------------------------------------------------------------
CREATE TABLE `fees` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `amount` DECIMAL(10, 2) NOT NULL,
    `due_date` DATE NOT NULL,
    `payment_date` DATE,
    `status` VARCHAR(20) NOT NULL,
    `transaction_ref` VARCHAR(100),
    FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------
-- SAMPLE DATA INSERTION (BCrypt Encoded Passwords - 'admin123', 'warden123', 'student123')
-- -----------------------------------------------------------------
-- Insert Rooms
INSERT INTO `rooms` (`id`, `room_number`, `floor`, `capacity`, `occupied_count`, `room_type`, `status`) VALUES
(1, 'A-101', 1, 2, 1, 'DOUBLE', 'AVAILABLE'),
(2, 'A-102', 1, 2, 0, 'DOUBLE', 'AVAILABLE'),
(3, 'B-201', 2, 1, 1, 'SINGLE', 'FULL'),
(4, 'B-202', 2, 3, 0, 'TRIPLE', 'MAINTENANCE');

-- Insert Users (Password is BCrypt for 'admin123', 'warden123', 'student123')
-- BCrypt for 'admin123': $2a$10$e8w.x7W10GqK4L2A4S5cTeYpUvLz.7OaN2I5i9X8P3z.V3R7pU5K2 (or BCrypt hash)
INSERT INTO `users` (`id`, `username`, `password`, `name`, `email`, `phone`, `role`, `created_at`) VALUES
(1, 'admin', '$2a$10$e8w.x7W10GqK4L2A4S5cTeYpUvLz.7OaN2I5i9X8P3z.V3R7pU5K2', 'Chief Admin', 'admin@hostel.com', '9876543210', 'ROLE_ADMIN', NOW()),
(2, 'warden', '$2a$10$e8w.x7W10GqK4L2A4S5cTeYpUvLz.7OaN2I5i9X8P3z.V3R7pU5K2', 'Dr. Robert Warden', 'warden@hostel.com', '9876543211', 'ROLE_WARDEN', NOW()),
(3, 'student1', '$2a$10$e8w.x7W10GqK4L2A4S5cTeYpUvLz.7OaN2I5i9X8P3z.V3R7pU5K2', 'Alice Smith', 'alice@student.com', '9876543212', 'ROLE_STUDENT', NOW()),
(4, 'student2', '$2a$10$e8w.x7W10GqK4L2A4S5cTeYpUvLz.7OaN2I5i9X8P3z.V3R7pU5K2', 'Bob Johnson', 'bob@student.com', '9876543213', 'ROLE_STUDENT', NOW());

-- Insert Admins
INSERT INTO `admins` (`id`, `department_code`) VALUES (1, 'ADMIN-MAIN');

-- Insert Wardens
INSERT INTO `wardens` (`id`, `block_assigned`) VALUES (2, 'Block A & B');

-- Insert Students
INSERT INTO `students` (`id`, `registration_number`, `department`, `year`, `gender`, `address`, `parent_name`, `parent_phone`, `blood_group`, `medical_condition`, `room_id`) VALUES
(3, '2026-CSE-001', 'Computer Science', 3, 'Female', '123 Tech Park, City', 'John Smith', '9123456789', 'O+', 'None', 1),
(4, '2026-ECE-002', 'Electronics', 2, 'Male', '456 Signal St, City', 'Mark Johnson', '9123456790', 'A+', 'Dust Allergy', 3);

-- Insert Complaints
INSERT INTO `complaints` (`id`, `student_id`, `title`, `description`, `status`, `created_at`, `resolved_at`, `resolution_note`) VALUES
(1, 3, 'Air Conditioner Noise', 'The AC unit in room A-101 makes loud vibrating noises during night hours.', 'PENDING', NOW(), NULL, NULL),
(2, 4, 'Plumbing Leakage in Bathroom', 'Water leaking under the washbasin tap.', 'RESOLVED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), 'Replaced washbasin washer and sealed pipe joints.');

-- Insert Fees
INSERT INTO `fees` (`id`, `student_id`, `amount`, `due_date`, `payment_date`, `status`, `transaction_ref`) VALUES
(1, 3, 25000.00, '2026-08-31', NULL, 'UNPAID', NULL),
(2, 4, 30000.00, '2026-07-15', '2026-07-10', 'PAID', 'TXN-984729104');
