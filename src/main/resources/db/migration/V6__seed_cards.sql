-- Seed data: 35 curated dinosaur cards with balanced 1-100 stats
-- Stats normalised for gameplay balance across height, weight, intelligence, speed, strength
-- Source: Zenodo CC0 dinosaur dataset (dino_clean.csv), stats manually curated

INSERT INTO cards (id, name, meaning, diet, era, image_url, height, weight, intelligence, speed, strength) VALUES
-- Carnivores
('a1000000-0000-0000-0000-000000000001', 'Tyrannosaurus Rex', 'Tyrant Lizard King', 'Carnivore', 'Late Cretaceous', NULL, 85, 95, 55, 50, 98),
('a1000000-0000-0000-0000-000000000002', 'Velociraptor', 'Swift Thief', 'Carnivore', 'Late Cretaceous', NULL, 20, 10, 92, 95, 35),
('a1000000-0000-0000-0000-000000000003', 'Spinosaurus', 'Spine Lizard', 'Carnivore', 'Late Cretaceous', NULL, 90, 90, 60, 40, 88),
('a1000000-0000-0000-0000-000000000004', 'Allosaurus', 'Different Lizard', 'Carnivore', 'Late Jurassic', NULL, 70, 75, 50, 55, 82),
('a1000000-0000-0000-0000-000000000005', 'Giganotosaurus', 'Giant Southern Lizard', 'Carnivore', 'Late Cretaceous', NULL, 88, 88, 48, 45, 95),
('a1000000-0000-0000-0000-000000000006', 'Dilophosaurus', 'Two-Crested Lizard', 'Carnivore', 'Early Jurassic', NULL, 45, 30, 55, 65, 50),
('a1000000-0000-0000-0000-000000000007', 'Carnotaurus', 'Meat-Eating Bull', 'Carnivore', 'Late Cretaceous', NULL, 55, 60, 40, 70, 72),
('a1000000-0000-0000-0000-000000000008', 'Baryonyx', 'Heavy Claw', 'Carnivore', 'Early Cretaceous', NULL, 50, 55, 58, 50, 65),
('a1000000-0000-0000-0000-000000000009', 'Deinonychus', 'Terrible Claw', 'Carnivore', 'Early Cretaceous', NULL, 25, 15, 88, 85, 45),
('a1000000-0000-0000-0000-000000000010', 'Megalosaurus', 'Great Lizard', 'Carnivore', 'Middle Jurassic', NULL, 55, 50, 45, 50, 70),
('a1000000-0000-0000-0000-000000000011', 'Compsognathus', 'Elegant Jaw', 'Carnivore', 'Late Jurassic', NULL, 5, 2, 65, 90, 8),
('a1000000-0000-0000-0000-000000000012', 'Ceratosaurus', 'Horned Lizard', 'Carnivore', 'Late Jurassic', NULL, 50, 45, 42, 55, 68),

-- Herbivores
('a1000000-0000-0000-0000-000000000013', 'Triceratops', 'Three-Horned Face', 'Herbivore', 'Late Cretaceous', NULL, 55, 85, 35, 30, 88),
('a1000000-0000-0000-0000-000000000014', 'Stegosaurus', 'Roof Lizard', 'Herbivore', 'Late Jurassic', NULL, 55, 70, 12, 25, 75),
('a1000000-0000-0000-0000-000000000015', 'Brachiosaurus', 'Arm Lizard', 'Herbivore', 'Late Jurassic', NULL, 98, 98, 25, 15, 60),
('a1000000-0000-0000-0000-000000000016', 'Ankylosaurus', 'Fused Lizard', 'Herbivore', 'Late Cretaceous', NULL, 35, 65, 30, 20, 85),
('a1000000-0000-0000-0000-000000000017', 'Diplodocus', 'Double Beam', 'Herbivore', 'Late Jurassic', NULL, 92, 80, 20, 18, 45),
('a1000000-0000-0000-0000-000000000018', 'Parasaurolophus', 'Near Crested Lizard', 'Herbivore', 'Late Cretaceous', NULL, 60, 55, 50, 45, 35),
('a1000000-0000-0000-0000-000000000019', 'Iguanodon', 'Iguana Tooth', 'Herbivore', 'Early Cretaceous', NULL, 55, 60, 45, 35, 50),
('a1000000-0000-0000-0000-000000000020', 'Pachycephalosaurus', 'Thick-Headed Lizard', 'Herbivore', 'Late Cretaceous', NULL, 40, 40, 35, 45, 70),
('a1000000-0000-0000-0000-000000000021', 'Apatosaurus', 'Deceptive Lizard', 'Herbivore', 'Late Jurassic', NULL, 88, 90, 18, 15, 55),
('a1000000-0000-0000-0000-000000000022', 'Protoceratops', 'First Horned Face', 'Herbivore', 'Late Cretaceous', NULL, 25, 20, 38, 40, 30),
('a1000000-0000-0000-0000-000000000023', 'Hadrosaurus', 'Sturdy Lizard', 'Herbivore', 'Late Cretaceous', NULL, 55, 55, 42, 40, 40),
('a1000000-0000-0000-0000-000000000024', 'Styracosaurus', 'Spiked Lizard', 'Herbivore', 'Late Cretaceous', NULL, 40, 50, 32, 30, 72),
('a1000000-0000-0000-0000-000000000025', 'Corythosaurus', 'Helmet Lizard', 'Herbivore', 'Late Cretaceous', NULL, 50, 50, 48, 42, 35),
('a1000000-0000-0000-0000-000000000026', 'Edmontosaurus', 'Edmonton Lizard', 'Herbivore', 'Late Cretaceous', NULL, 58, 60, 40, 38, 38),
('a1000000-0000-0000-0000-000000000027', 'Maiasaura', 'Good Mother Lizard', 'Herbivore', 'Late Cretaceous', NULL, 48, 50, 55, 35, 32),

-- Omnivores
('a1000000-0000-0000-0000-000000000028', 'Gallimimus', 'Chicken Mimic', 'Omnivore', 'Late Cretaceous', NULL, 35, 25, 62, 92, 22),
('a1000000-0000-0000-0000-000000000029', 'Oviraptor', 'Egg Thief', 'Omnivore', 'Late Cretaceous', NULL, 22, 18, 72, 70, 25),
('a1000000-0000-0000-0000-000000000030', 'Ornithomimus', 'Bird Mimic', 'Omnivore', 'Late Cretaceous', NULL, 30, 22, 60, 88, 20),
('a1000000-0000-0000-0000-000000000031', 'Therizinosaurus', 'Scythe Lizard', 'Omnivore', 'Late Cretaceous', NULL, 72, 70, 35, 25, 78),

-- Flying / Marine (for variety)
('a1000000-0000-0000-0000-000000000032', 'Pteranodon', 'Toothless Wing', 'Carnivore', 'Late Cretaceous', NULL, 30, 15, 58, 80, 18),
('a1000000-0000-0000-0000-000000000033', 'Archaeopteryx', 'Ancient Wing', 'Carnivore', 'Late Jurassic', NULL, 8, 3, 55, 72, 5),
('a1000000-0000-0000-0000-000000000034', 'Mosasaurus', 'Meuse River Lizard', 'Carnivore', 'Late Cretaceous', NULL, 80, 92, 52, 60, 90),
('a1000000-0000-0000-0000-000000000035', 'Plesiosaurus', 'Near Lizard', 'Carnivore', 'Early Jurassic', NULL, 60, 45, 48, 55, 55);
