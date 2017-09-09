
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[user_tbl](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NULL,
	[age] [int] NULL
) ON [PRIMARY]

