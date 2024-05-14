CREATE TABLE `roles` (
  `id` INT AUTO_INCREMENT NOT NULL,
  `name` VARCHAR(250) NOT NULL,
  `description` TEXT(1000) NULL,
  `created_at` DATETIME NOT NULL,
  `created_by` VARCHAR(250) NOT NULL,
  `updated_at` DATETIME NULL,
  `updated_by` VARCHAR(250) NULL,
   PRIMARY KEY (`id`)
)