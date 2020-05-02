module.exports = {
  siteMetadata: {
    title: `Spellsource`,
  },
  plugins: [
    {
      resolve: `gatsby-source-filesystem`,
      options: {
        name: `src`,
        path: `${__dirname}/src/`,
      },
    },
    {
      resolve: `gatsby-source-filesystem`,
      options: {
        name: `src`,
        path: `${__dirname}/../unityclient/Assets/UBlockly/JsonBlocks/`,
      },
    }, {
      resolve: `gatsby-source-filesystem`,
      options: {
        name: `src`,
        path: `${__dirname}/../unityclient/Assets/UBlockly/Toolboxes/Configs`,
      },
    },
    {
      resolve: `gatsby-transformer-json-hooks`,
      options: {
        onTransformObject: ({ fileNode, object }) => {
          // this is a card JSON
          if (object.hasOwnProperty('fileFormatVersion')) {
            if (!object.id) {
              // Set the id
              object.id = fileNode.base.replace(/.json$/, '')
            }
            // Also set a path on the cards node which corresponds to its URL in the website
            object.path = '/cards/' + object.id
          } else if ((object.hasOwnProperty('args0') || object.hasOwnProperty('message0')) && object.hasOwnProperty('type')) {
            // this is a blockly block
            if (!object.id && !!object.type) {
              object.id = object.type
            }

            object.path = '/blocks/' + object.id
          } else if (object.hasOwnProperty('Style') && object.hasOwnProperty('BlockCategoryList')) {
            // this is a toolbox definition
            if (!object.id && !!object.Style) {
              object.id = object.Style
            }
            object.path = '/toolboxes/' + object.id
          }
        },
        typeName: ({ node, object, isArray }) => {
          // This is card JSON
          if (object.hasOwnProperty('fileFormatVersion')) {
            return 'Card'
          } else if ((object.hasOwnProperty('args0') || object.hasOwnProperty('message0')) && object.hasOwnProperty('type')) {
            // this is a blockly block
            return 'Block'
          } else if (object.hasOwnProperty('Style') && object.hasOwnProperty('BlockCategoryList')) {
            // this is a toolbox definition
            return 'Toolbox'
          }
          // return 'Json'
        }
      }
    },
    {
      resolve: `gatsby-source-filesystem`,
      options: {
        path: `${__dirname}/../cards/src/main/resources/cards/custom`,
      },
    },
    `gatsby-plugin-sass`,
    `gatsby-plugin-sharp`,
    `gatsby-image`,
    `gatsby-transformer-sharp`,
    {
      resolve: `gatsby-transformer-remark`,
      options: {
        plugins: [
          `gatsby-remark-copy-linked-files`,
          {
            resolve: `gatsby-remark-images`,
            options: {
              // It's important to specify the maxWidth (in pixels) of
              // the content container as this plugin uses this as the
              // base for generating different widths of each image.
              maxWidth: 650,
              linkImagesToOriginal: false,
              backgroundColor: 'transparent',
              withWebp: true,
              disableBgImage: true,
              quality: 100,
            },
          },
        ],
      },
    },
    {
      resolve: `gatsby-plugin-s3`,
      options: {
        bucketName: 'www.playspellsource.com',
        protocol: 'https',
        hostname: 'www.playspellsource.com',
      },
    },
    {
      resolve: `@gatsby-contrib/gatsby-plugin-elasticlunr-search`,
      options: {
        // Fields to index
        fields: [`title`, `tags`, `rawMarkdownBody`],
        // How to resolve each field`s value for a supported node type
        resolvers: {
          // For any node of type MarkdownRemark, list how to resolve the fields` values
          MarkdownRemark: {
            title: node => node.frontmatter.title,
            tags: node => node.frontmatter.tags,
            path: node => node.frontmatter.path,
            rawMarkdownBody: node => node.rawMarkdownBody
          },
          Card: {
            // TODO: Change the name of the field to be its content
            title: node => node.name,
            tags: node => '', // [node.type, ...(Object.keys(node.attributes) || [])].join(', ')
            rawMarkdownBody: node => node.description,
            path: node => node.path
          }
        },
        // Optional filter to limit indexed nodes
        filter: (node, getNode) => true
      },
    },
  ],
}