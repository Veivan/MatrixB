
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mApplications](
	[id_app] [bigint] IDENTITY(1,1) NOT NULL,
	[cons_key] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[cons_secret] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[user_id] [bigint] NOT NULL,
	[appname] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[active] [smallint] NOT NULL
) ON [PRIMARY]

ALTER TABLE [dbo].[mApplications] ADD  CONSTRAINT [DF_mApplications_active]  DEFAULT ((1)) FOR [active]
