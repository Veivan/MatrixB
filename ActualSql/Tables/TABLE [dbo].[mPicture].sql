
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mPicture](
	[pic_id] [int] IDENTITY(1,1) NOT NULL,
	[gender] [bit] NULL,
	[fpicture] [varbinary](max) NULL,
	[ptype_id] [int] NULL
) ON [PRIMARY]

