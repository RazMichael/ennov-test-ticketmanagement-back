-- Create 2 users
INSERT INTO "user" ("id", "username", "email") VALUES
('1', 'usertest1', 'usertest1@ticketmanagement.com'),
('2', 'usertest2', 'usertest2@ticketmanagement.com');

-- Give usertest1 some tickets
INSERT INTO "ticket" ("id", "title", "description", "status", "user_id", "user_creator_id") VALUES
('1', 'ticket 1', 'description ticket 1', 1, '1', '1'),
('2', 'ticket 2', 'description ticket 2', 2, '1', '1'),
('3', 'ticket 3', 'description ticket 3', 0, '1', '1');