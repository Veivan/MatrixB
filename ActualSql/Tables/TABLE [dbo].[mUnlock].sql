
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mUnlock](
	[un_id] [int] IDENTITY(1,1) NOT NULL,
	[ae_id] [bigint] NOT NULL,
	[dunlock] [datetime] NULL
) ON [PRIMARY]

