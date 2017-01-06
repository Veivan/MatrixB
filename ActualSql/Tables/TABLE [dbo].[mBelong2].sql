
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mBelong2](
	[bg_id] [bigint] IDENTITY(1,1) NOT NULL,
	[GroupID] [int] NOT NULL,
	[user_id] [bigint] NOT NULL
) ON [PRIMARY]

EXEC sys.sp_addextendedproperty @name=N'Description', @value=N'Accounts belongs 2 Groups' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'mBelong2'
