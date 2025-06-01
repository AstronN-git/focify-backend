insert into tb_users (email, username, password) values
                                                     ('mail@mailservice.com', 'Maxim', '$2a$12$hJZt5kGT4hi5uveS3Rh7vu.OB4scEUhgxinHXqHQL.ojM.8GTy6Fu'), -- 12345
                                                     ('mail2@mail.com', 'Alex', '$2a$12$r1qQvNpEi/UnnJpgg8ElW.Si1Dp6KtEcV5irZOOiBXh9.RWAOk1lK'); -- qwert


insert into tb_publications (author_id, duration, description) values
                                                     (1, 1500, 'Doing some staff'),
                                                     (1, 2000, 'Session of vibe coding'),
                                                     (1, 2300, 'Gone to school'),
                                                     (2, 1500, 'Watched math lecture'),
                                                     (2, 2500, 'Made all homework for tomorrow');

insert into tb_publications (author_id, duration, description, created_at) values
                                                     (2, 1366, 'Was preparing for my math exam while listening to LoFi girl', '2025-05-03 13:45:12')