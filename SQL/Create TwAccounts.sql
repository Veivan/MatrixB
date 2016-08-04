USE [MatrixB]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[TwAccounts](
	[user_id] [bigint] NOT NULL,
	[name] [nvarchar](50) NOT NULL,
	[screen_name] [nvarchar](150) NOT NULL,
	[location] [nvarchar](250) NULL,
	[followers_count] [int] NULL,
	[friends_count] [int] NULL,
	[listed_count] [int] NULL,
	[statuses_count] [int] NULL,
	[url] [nvarchar](250) NULL,
	[description] [nvarchar](max) NULL,
	[created_at] [datetimeoffset](4) NULL,
	[utc_offset] [int] NULL,
	[time_zone] [nvarchar](150) NULL,
	[lang_id] [int] NULL,
	[geo_enabled] [bit] NULL,
	[lasttweet_at] [datetime] NULL,
	[default_profile] [bit] NULL,
	[default_profile_image] [bit] NULL,
	[verified] [bit] NULL,
	[finsert] [datetime] NULL,
	[sinsert] [datetime] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO


