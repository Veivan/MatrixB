
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mTasks](
	[id_Task] [bigint] IDENTITY(1,1) NOT NULL,
	[TaskDate] [datetime] NOT NULL,
	[id_TaskType] [smallint] NOT NULL,
	[TContent] [nvarchar](max) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[IsRepeat] [bit] NOT NULL
) ON [PRIMARY]

