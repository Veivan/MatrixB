
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mApplications](
	[id_app] [bigint] IDENTITY(1,1) NOT NULL,
	[cons_key] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[cons_secret] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[user_id] [bigint] NOT NULL,
	[appname] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL
) ON [PRIMARY]

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Consumer key' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'mApplications', @level2type=N'COLUMN',@level2name=N'cons_key'
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Consumer secret' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'mApplications', @level2type=N'COLUMN',@level2name=N'cons_secret'
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Application owner' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'mApplications', @level2type=N'COLUMN',@level2name=N'user_id'
