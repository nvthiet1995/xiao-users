CREATE TABLE `users` (
    `id` int AUTO_INCREMENT  PRIMARY KEY,
    `username` varchar(20) NOT NULL,
    `password` varchar(255) NOT NULL,
    `email_address` varchar(50) NOT NULL,
    `created_at` DATETIME NOT NULL,
    `created_by` varchar(20) NOT NULL,
    `updated_at` DATETIME DEFAULT NULL,
    `updated_by` varchar(20) DEFAULT NULL
);