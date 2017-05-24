
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mGroupRegim](
	[grg_id] [int] IDENTITY(1,1) NOT NULL,
	[groupid] [int] NOT NULL,
	[WakeHour] [int] NOT NULL,
	[BedHour] [int] NOT NULL
) ON [PRIMARY]

