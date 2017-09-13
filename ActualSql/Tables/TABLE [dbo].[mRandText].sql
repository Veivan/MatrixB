
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mRandText](
	[rnt_id] [bigint] IDENTITY(1,1) NOT NULL,
	[randtext] [nvarchar](max) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[pic_id] [int] NULL,
	[url] [nvarchar](300) COLLATE Cyrillic_General_CI_AS NULL,
	[twit_id] [int] NOT NULL
) ON [PRIMARY]

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Link to DicTwType' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'mRandText', @level2type=N'COLUMN',@level2name=N'twit_id'
