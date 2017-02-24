
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[DicLang](
	[lang_id] [int] IDENTITY(1,1) NOT NULL,
	[lang] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL
) ON [PRIMARY]

