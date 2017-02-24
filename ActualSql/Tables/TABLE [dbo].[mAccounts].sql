
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
	[created_at] [datetimeoffset](2) NULL,
	[utc_offset] [int] NULL,
	[time_zone] [nvarchar](150) COLLATE Cyrillic_General_CI_AS NULL,
	[lang_id] [int] NULL,
	[geo_enabled] [bit] NULL,
	[lasttweet_at] [datetimeoffset](2) NULL,
	[default_profile] [bit] NULL,
	[default_profile_image] [bit] NULL,
	[verified] [bit] NULL,
	[finsert] [smalldatetime] NULL,
	[sinsert] [smalldatetime] NULL,
	[email] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL,
	[phone] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL,
	[pass] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL,
	[twitter_id] [bigint] NULL,
	[mailpass] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL,
	[gender] [bit] NULL,
	[enabled] [bit] NULL
) ON [PRIMARY]

