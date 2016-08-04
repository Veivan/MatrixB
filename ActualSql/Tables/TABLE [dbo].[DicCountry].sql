
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[DicCountry](
	[id_cn] [int] IDENTITY(1,1) NOT NULL,
	[country_code] [nvarchar](10) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[description] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL
) ON [PRIMARY]

