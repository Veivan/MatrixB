
  DECLARE @tw2 NVARCHAR(MAX) =
	'{"command":"TWIT", "tags":["#helpchildren","Дети"], "lat":"55.751244", "lon":"37.618423"}' 
  
  INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat])
  VALUES ('2017-02-25 00:00:00', 12, '{"command": "READHOMETIMELINE"}', 1)

  INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat])
  VALUES ('2017-02-25 00:00:00', 8, '{"command": "FOLLOW"}', 1)

  INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat])
  VALUES ('2017-02-25 00:00:00', 4, '{"command": "RETWIT"}', 1)

  INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat])
  VALUES ('2017-02-25 00:00:00', 4, '{"command": "RETWIT"}', 1)

  INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat])
  VALUES ('2017-02-25 00:00:00', 3, @tw2, 1)

  INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat])
  VALUES ('2017-02-25 00:00:00', 12, '{"command": "READHOMETIMELINE"}', 1)

  INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat])
  VALUES ('2017-02-25 00:00:00', 4, '{"command": "RETWIT"}', 1)

  INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat])
  VALUES ('2017-02-25 00:00:00', 4, '{"command": "RETWIT"}', 1)

  INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat])
  VALUES ('2017-02-25 00:00:00', 3, @tw2, 1)

  INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat])
  VALUES ('2017-02-27 00:00:00', 4, '{"command": "LIKE"}', 1)

  INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat])
  VALUES ('2017-02-27 00:00:00', 4, '{"command": "LIKE"}', 1)
