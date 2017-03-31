
	DECLARE @tw2 NVARCHAR(MAX) =
		'{"command":"TWIT", "tags":["#helpchildren","Дети"], "lat":"55.751244", "lon":"37.618423"}' 

	DECLARE @dexec NVARCHAR(MAX) = '2017-04-01 00:00:00'
  
	--INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	--VALUES ('2017-02-25 00:00:00', 12, '{"command": "VISIT", "url" : "http://veivan.ucoz.ru"}', 1, 100)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 12, '{"command": "READHOMETIMELINE"}', 1, 200)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 8, '{"command": "FOLLOW"}', 1, 300)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 4, '{"command": "RETWIT"}', 1, 400)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 4, '{"command": "RETWIT" , "twit_id" : "842391722458025990"}', 0, 410)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 5, '{"command": "LIKE" , "twit_id" : "842391722458025990"}', 0, 420)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 3, @tw2, 1, 500)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 4, '{"command": "RETWIT"}', 1, 600)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 12, '{"command": "READHOMETIMELINE"}', 1, 700)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 4, '{"command": "RETWIT"}', 1, 800)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 5, '{"command": "LIKE"}', 1, 900)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 3, @tw2, 1, 1000)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 4, '{"command": "RETWIT"}', 1, 1100)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 5, '{"command": "LIKE"}', 1, 1200)

	INSERT [dbo].[mTasks] ([TaskDate],[id_TaskType],[TContent],[IsRepeat],[ordernum])
	VALUES (@dexec, 4, '{"command": "RETWIT"}', 1, 1300)

