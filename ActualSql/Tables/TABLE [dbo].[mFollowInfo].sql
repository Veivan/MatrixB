
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mFollowInfo](
	[fw_id] [bigint] IDENTITY(1,1) NOT NULL,
	[user_id] [bigint] NOT NULL,
	[twitter_id] [bigint] NOT NULL,
	[fwtype] [bit] NULL,
	[date_upd] [datetime] NOT NULL
) ON [PRIMARY]

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'0-follower 1- friend  null - unfollow' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'mFollowInfo', @level2type=N'COLUMN',@level2name=N'fwtype'
