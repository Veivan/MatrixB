
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mTokens](
	[id_creds] [bigint] IDENTITY(1,1) NOT NULL,
	[user_id] [bigint] NOT NULL,
	[id_app] [bigint] NOT NULL,
	[token] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[token_secret] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL
) ON [PRIMARY]

