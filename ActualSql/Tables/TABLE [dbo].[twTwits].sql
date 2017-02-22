
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[twTwits](
	[tw_id] [bigint] NOT NULL,
	[user_id] [bigint] NOT NULL,
	[status] [nvarchar](1000) COLLATE Cyrillic_General_CI_AS NULL,
	[creator_id] [bigint] NULL,
	[created_at] [datetimeoffset](2) NULL,
	[favorite_count] [int] NULL,
	[in_reply_to_screen_name] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL,
	[in_reply_to_status_id] [bigint] NULL,
	[in_reply_to_user_id] [bigint] NULL,
	[lang] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL,
	[retweet_count] [int] NULL,
	[text] [nvarchar](500) COLLATE Cyrillic_General_CI_AS NULL,
	[place_json] [nvarchar](500) COLLATE Cyrillic_General_CI_AS NULL,
	[coordinates_json] [nvarchar](500) COLLATE Cyrillic_General_CI_AS NULL,
	[favorited] [bit] NULL,
	[retweeted] [bit] NULL,
	[isRetweet] [bit] NULL
) ON [PRIMARY]

