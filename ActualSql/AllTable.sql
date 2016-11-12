
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[DicCountry](
	[id_cn] [int] IDENTITY(1,1) NOT NULL,
	[country_code] [nvarchar](10) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[description] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL
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
	[user_id] [bigint] NOT NULL,
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
	[pass] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL
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

