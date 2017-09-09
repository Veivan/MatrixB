
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mRandText](
	[rnt_id] [bigint] IDENTITY(1,1) NOT NULL,
	[randtext] [nvarchar](max) COLLATE Cyrillic_General_CI_AS NOT NULL
) ON [PRIMARY]

