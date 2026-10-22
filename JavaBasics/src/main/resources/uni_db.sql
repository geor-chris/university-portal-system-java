-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Εξυπηρετητής: 127.0.0.1
-- Χρόνος δημιουργίας: 22 Οκτ 2026 στις 15:45:29
-- Έκδοση διακομιστή: 10.4.32-MariaDB
-- Έκδοση PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Βάση δεδομένων: `uni_db`
--

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `courses`
--

CREATE TABLE `courses` (
  `course_name` varchar(100) NOT NULL,
  `course_description` text NOT NULL,
  `ects` int(11) NOT NULL,
  `professor_id` int(11) DEFAULT NULL,
  `semester` int(11) NOT NULL,
  `department` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `courses`
--

INSERT INTO `courses` (`course_name`, `course_description`, `ects`, `professor_id`, `semester`, `department`) VALUES
('Data Structures and Algorithms', 'Data Structures and Algorithms (DSA) are the two foundational building blocks of computer science used to create efficient software', 5, 23, 2, 'IT'),
('Discrete Mathematics', 'the study of mathematical structures that use distinct, separated, and countable values (such as integers, graphs, and logical statements) rather than smooth or continuous ranges.', 5, 24, 2, 'IT'),
('Generative AI & LLM Systems', 'Generative AI is a broad branch of artificial intelligence that creates new content—such as text, images, video, audio, and code—from user prompts.\r\n\r\nLarge Language Models (LLMs) are a specialized subset of Generative AI focused exclusively on natural language understanding, processing, and text or code generation.', 5, 23, 1, 'IT');

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `grades`
--

CREATE TABLE `grades` (
  `grade_id` int(11) NOT NULL,
  `am` int(11) NOT NULL,
  `course_name` varchar(100) NOT NULL,
  `bathmos` int(11) NOT NULL,
  `description` text NOT NULL,
  `professor_id` int(11) NOT NULL,
  `semester` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `grades`
--

INSERT INTO `grades` (`grade_id`, `am`, `course_name`, `bathmos`, `description`, `professor_id`, `semester`) VALUES
(8, 0, 'Data Structures and Algorithms', 8, '', 23, 1),
(9, 2, 'Data Structures and Algorithms', 7, '', 23, 2),
(10, 0, 'Generative AI & LLM Systems', 4, '', 23, 1),
(11, 2, 'Generative AI & LLM Systems', 9, '', 23, 2),
(12, 0, 'Discrete Mathematics', 9, '', 24, 1),
(13, 2, 'Discrete Mathematics', 7, '', 24, 2);

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `pending_grades`
--

CREATE TABLE `pending_grades` (
  `am` int(11) NOT NULL,
  `course_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `professors`
--

CREATE TABLE `professors` (
  `id` int(11) NOT NULL,
  `major` varchar(100) NOT NULL,
  `office` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `professors`
--

INSERT INTO `professors` (`id`, `major`, `office`) VALUES
(23, 'Network Engineer', 'First floor, nine'),
(24, 'Applied Physics', 'First floor, first door');

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `secretaries`
--

CREATE TABLE `secretaries` (
  `id` int(11) NOT NULL,
  `office` varchar(50) NOT NULL,
  `work_hours` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `secretaries`
--

INSERT INTO `secretaries` (`id`, `office`, `work_hours`) VALUES
(20, 'First floor, number ten', '8:00am-4:00pm');

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `students`
--

CREATE TABLE `students` (
  `id` int(11) NOT NULL,
  `am` int(11) NOT NULL,
  `semester` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `students`
--

INSERT INTO `students` (`id`, `am`, `semester`) VALUES
(21, 0, 1),
(22, 2, 2);

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(50) NOT NULL,
  `surname` varchar(50) NOT NULL,
  `department` varchar(100) NOT NULL,
  `uni_role` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `name`, `surname`, `department`, `uni_role`) VALUES
(20, 'secretary', '$2a$10$rKTLHgeRSt2v1vPVIsz9sOivk3zEkF9grGS/zHuYVRldU4fwJWmhK', 'Secretary_Admin', 'Admin_Secretary', 'IT', 'Secretary'),
(21, 'student', '$2a$10$6hHe8MOhe1oh87DeI9s3Wu6zxkpphzGOiTQi0usdc7SETze7dcXPW', 'Student_Example', 'Example_Student', 'IT', 'Student'),
(22, 'student2', '$2a$10$6r0kqqC.qIuziJuWB13y6uzDTYbbRq80ExjOuY9T9OFKwQt9bGyr2', 'Student_Example2', 'Example2_Student', 'IT', 'Student'),
(23, 'professor', '$2a$10$tex7hxC/RQSQxpfv25Nkb.QwsPRivyZJRRIeEEQrrGeo0nhihfJrm', 'Professor_Example', 'Example_Professor', 'IT', 'Professor'),
(24, 'professor2', '$2a$10$3TH56V/PL5tFTrtKZvWA5uYIsoaBstpxkbkqw3LKlC5C.T4r7WCZS', 'Professor_Example2', 'Example2_Professor', 'IT', 'Professor');

--
-- Ευρετήρια για άχρηστους πίνακες
--

--
-- Ευρετήρια για πίνακα `courses`
--
ALTER TABLE `courses`
  ADD PRIMARY KEY (`course_name`),
  ADD KEY `professor_id` (`professor_id`);

--
-- Ευρετήρια για πίνακα `grades`
--
ALTER TABLE `grades`
  ADD PRIMARY KEY (`grade_id`),
  ADD KEY `am` (`am`),
  ADD KEY `course_name` (`course_name`),
  ADD KEY `professor_id` (`professor_id`);

--
-- Ευρετήρια για πίνακα `pending_grades`
--
ALTER TABLE `pending_grades`
  ADD PRIMARY KEY (`am`,`course_name`);

--
-- Ευρετήρια για πίνακα `professors`
--
ALTER TABLE `professors`
  ADD PRIMARY KEY (`id`);

--
-- Ευρετήρια για πίνακα `secretaries`
--
ALTER TABLE `secretaries`
  ADD PRIMARY KEY (`id`);

--
-- Ευρετήρια για πίνακα `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `am` (`am`);

--
-- Ευρετήρια για πίνακα `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT για άχρηστους πίνακες
--

--
-- AUTO_INCREMENT για πίνακα `grades`
--
ALTER TABLE `grades`
  MODIFY `grade_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT για πίνακα `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- Περιορισμοί για άχρηστους πίνακες
--

--
-- Περιορισμοί για πίνακα `courses`
--
ALTER TABLE `courses`
  ADD CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`professor_id`) REFERENCES `professors` (`id`);

--
-- Περιορισμοί για πίνακα `grades`
--
ALTER TABLE `grades`
  ADD CONSTRAINT `grades_ibfk_1` FOREIGN KEY (`am`) REFERENCES `students` (`am`),
  ADD CONSTRAINT `grades_ibfk_2` FOREIGN KEY (`course_name`) REFERENCES `courses` (`course_name`),
  ADD CONSTRAINT `grades_ibfk_3` FOREIGN KEY (`professor_id`) REFERENCES `professors` (`id`);

--
-- Περιορισμοί για πίνακα `professors`
--
ALTER TABLE `professors`
  ADD CONSTRAINT `professors_ibfk_1` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Περιορισμοί για πίνακα `secretaries`
--
ALTER TABLE `secretaries`
  ADD CONSTRAINT `secretaries_ibfk_1` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Περιορισμοί για πίνακα `students`
--
ALTER TABLE `students`
  ADD CONSTRAINT `students_ibfk_1` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
