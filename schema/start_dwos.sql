
CREATE TABLE `seq_num` (
  `pk_name` varchar(32) NOT NULL,
  `pk_next_id` bigint(20) NOT NULL,
  UNIQUE KEY `pk_name` (`pk_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO seq_num (pk_name, pk_next_id) VALUES('global_id', 1000);

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `email` varchar(300) NOT NULL,
  `password` varchar(50) NOT NULL,
  `cell` varchar(15) NOT NULL,
  `user_type` varchar(30) NOT NULL,
  `user_status` varchar(30) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `user_session` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `session_key` varchar(40) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_on` timestamp NULL DEFAULT NULL,
  `expired_on` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_session_key_uk` (`session_key`),
  KEY `user_session_fk` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=552 DEFAULT CHARSET=utf8;