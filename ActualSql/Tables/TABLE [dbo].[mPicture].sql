
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mPicture](
	[pic_id] [int] IDENTITY(1,1) NOT NULL,
	[gender] [bit] NULL,
	[fpicture] [varbinary](max) NULL,
	[ptype_id] [int] NULL,
	[isused] [bit] NULL
) ON [PRIMARY]

ALTER TABLE [dbo].[mPicture] ADD  CONSTRAINT [DF_mPicture_isused]  DEFAULT ((0)) FOR [isused]
