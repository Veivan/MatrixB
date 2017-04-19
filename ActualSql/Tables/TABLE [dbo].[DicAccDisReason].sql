
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[DicAccDisReason](
	[adr_id] [int] IDENTITY(1,1) NOT NULL,
	[errorcode] [int] NULL,
	[comment] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL
) ON [PRIMARY]

