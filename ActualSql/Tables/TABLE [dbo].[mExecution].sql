
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mExecution](
	[ae_id] [bigint] IDENTITY(1,1) NOT NULL,
	[user_id] [bigint] NOT NULL,
	[id_task] [bigint] NOT NULL,
	[act_id] [bigint] NOT NULL,
	[result] [bit] NOT NULL,
	[failreason] [nvarchar](max) COLLATE Cyrillic_General_CI_AS NULL,
	[execdate] [bigint] NOT NULL,
	[fr_id] [int] NULL
) ON [PRIMARY]

