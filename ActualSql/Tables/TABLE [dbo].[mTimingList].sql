
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mTimingList](
	[ctm_id] [bigint] IDENTITY(1,1) NOT NULL,
	[tmng_id] [bigint] NOT NULL,
	[id_TaskType] [smallint] NOT NULL,
	[tstamp] [bigint] NOT NULL
) ON [PRIMARY]

