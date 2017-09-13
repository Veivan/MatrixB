
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[DicTwType](
	[id_prj] [int] IDENTITY(1,1) NOT NULL,
	[twit_id] [int] NOT NULL,
	[descript] [nvarchar](1000) COLLATE Cyrillic_General_CI_AS NOT NULL
) ON [PRIMARY]

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Тип проекта. Задаётся в TContent задачи.' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'DicTwType', @level2type=N'COLUMN',@level2name=N'twit_id'
