-- Fix missing fun facts for cards not covered in V14
-- V14 had 6 wrong card names (Maiasaura, Kentrosaurus, Edmontosaurus, Microraptor, Dimetrodon, Quetzalcoatlus)
-- which do not exist in our card set. The 6 cards below were left without fun facts.

UPDATE cards SET fun_fact = 'Tenontosaurus fossils are almost always found near Deinonychus teeth — strong evidence that packs of Deinonychus hunted them regularly.' WHERE name = 'Tenontosaurus';
UPDATE cards SET fun_fact = 'Beipiaosaurus was the first large dinosaur found with feathers, proving feathers weren''t just for small bird-like species.' WHERE name = 'Beipiaosaurus';
UPDATE cards SET fun_fact = 'Pterodactyl is actually the informal name — the real genus is Pterodactylus, one of the first pterosaurs ever discovered in 1784.' WHERE name = 'Pterodactyl';
UPDATE cards SET fun_fact = 'Ornithomimus had no teeth at all — its beak was perfectly designed for an omnivorous diet of plants, insects, and small animals.' WHERE name = 'Ornithomimus';
UPDATE cards SET fun_fact = 'Deinosuchus was a giant crocodilian that preyed on dinosaurs — fossil bite marks on dinosaur bones confirm it attacked animals far larger than itself.' WHERE name = 'Deinosuchus';
UPDATE cards SET fun_fact = 'Troodon had the largest brain relative to its body size of any known dinosaur, leading some scientists to speculate it could have evolved human-like intelligence given more time.' WHERE name = 'Troodon';
