
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[twTwits](
	[tw_id] [bigint] NOT NULL,
	[status] [nvarchar](1000) COLLATE Cyrillic_General_CI_AS NULL,
	[created_at] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL,
	[favorite_count] [int] NULL,
	[in_reply_to_screen_name] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL,
	[in_reply_to_status_id] [bigint] NULL,
	[in_reply_to_user_id] [bigint] NULL,
	[lang] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL,
	[retweet_count] [int] NULL,
	[text] [nvarchar](500) COLLATE Cyrillic_General_CI_AS NULL,
	[user_id] [bigint] NULL,
	[place_json] [nvarchar](500) COLLATE Cyrillic_General_CI_AS NULL,
	[coordinates_json] [nvarchar](500) COLLATE Cyrillic_General_CI_AS NULL
) ON [PRIMARY]

