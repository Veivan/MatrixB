
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[DicProxyType](
	[prtypeID] [tinyint] IDENTITY(1,1) NOT NULL,
	[typename] [nchar](10) COLLATE Cyrillic_General_CI_AS NOT NULL
) ON [PRIMARY]

