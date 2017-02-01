
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[DicGroups](
	[group_id] [int] IDENTITY(1,1) NOT NULL,
	[group_name] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[dcreate] [date] NULL
) ON [PRIMARY]

ALTER TABLE [dbo].[DicGroups] ADD  CONSTRAINT [DF_DicGroups_dcreate]  DEFAULT (getdate()) FOR [dcreate]
