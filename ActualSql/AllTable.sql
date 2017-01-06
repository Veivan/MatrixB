
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[DicCountry](
	[id_cn] [int] IDENTITY(1,1) NOT NULL,
	[country_code] [nvarchar](10) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[description] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL
) ON [PRIMARY]


SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[DicGroups](
	[group_id] [int] IDENTITY(1,1) NOT NULL,
	[group_name] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL
) ON [PRIMARY]


SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[DicProxyType](
	[prtypeID] [tinyint] IDENTITY(1,1) NOT NULL,
	[typename] [nchar](10) COLLATE Cyrillic_General_CI_AS NOT NULL
) ON [PRIMARY]


SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[DicTaskType](
	[id_TaskType] [smallint] NOT NULL,
	[TypeMean] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL
) ON [PRIMARY]


SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mAccounts](
	[user_id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[screen_name] [nvarchar](150) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[location] [nvarchar](250) COLLATE Cyrillic_General_CI_AS NULL,
	[followers_count] [int] NULL,
	[friends_count] [int] NULL,
	[listed_count] [int] NULL,
	[statuses_count] [int] NULL,
	[url] [nvarchar](250) COLLATE Cyrillic_General_CI_AS NULL,
	[description] [nvarchar](max) COLLATE Cyrillic_General_CI_AS NULL,
	[created_at] [datetimeoffset](4) NULL,
	[utc_offset] [int] NULL,
	[time_zone] [nvarchar](150) COLLATE Cyrillic_General_CI_AS NULL,
	[lang_id] [int] NULL,
	[geo_enabled] [bit] NULL,
	[lasttweet_at] [datetime] NULL,
	[default_profile] [bit] NULL,
	[default_profile_image] [bit] NULL,
	[verified] [bit] NULL,
	[finsert] [datetime] NULL,
	[sinsert] [datetime] NULL,
	[email] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL,
	[phone] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL,
	[pass] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL,
	[twitter_id] [bigint] NULL
) ON [PRIMARY]


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

SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mBelong2](
	[bg_id] [bigint] IDENTITY(1,1) NOT NULL,
	[group_id] [int] NOT NULL,
	[user_id] [bigint] NOT NULL
) ON [PRIMARY]

EXEC sys.sp_addextendedproperty @name=N'Description', @value=N'Accounts belongs 2 Groups' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'mBelong2'

SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mDBversion](
	[version] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL
) ON [PRIMARY]


SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mExecution](
	[ae_id] [bigint] IDENTITY(1,1) NOT NULL,
	[user_id] [bigint] NOT NULL,
	[id_task] [bigint] NOT NULL,
	[act_id] [bigint] NOT NULL,
	[result] [bit] NOT NULL,
	[failreason] [nvarchar](max) COLLATE Cyrillic_General_CI_AS NULL,
	[execdate] [bigint] NOT NULL
) ON [PRIMARY]


SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mProxies](
	[ProxyID] [bigint] IDENTITY(1,1) NOT NULL,
	[ip] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[port] [int] NOT NULL,
	[prtypeID] [tinyint] NOT NULL,
	[id_cn] [int] NOT NULL,
	[alive] [bit] NOT NULL
) ON [PRIMARY]


SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mProxyAcc](
	[acprID] [bigint] IDENTITY(1,1) NOT NULL,
	[user_id] [bigint] NOT NULL,
	[ProxyID] [bigint] NOT NULL
) ON [PRIMARY]


SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mTasks](
	[id_Task] [bigint] IDENTITY(1,1) NOT NULL,
	[TaskDate] [datetime] NOT NULL,
	[id_TaskType] [smallint] NOT NULL,
	[TContent] [nvarchar](max) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[IsRepeat] [bit] NOT NULL
) ON [PRIMARY]


SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mTokens](
	[id_creds] [bigint] IDENTITY(1,1) NOT NULL,
	[user_id] [bigint] NOT NULL,
	[id_app] [bigint] NOT NULL,
	[token] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[token_secret] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL
) ON [PRIMARY]

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Access token' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'mTokens', @level2type=N'COLUMN',@level2name=N'token'
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Access token secret' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'mTokens', @level2type=N'COLUMN',@level2name=N'token_secret'
